package com.dianping.pigeon.remoting.provider.config.spring;

import com.dianping.pigeon.config.ConfigManager;
import com.dianping.pigeon.config.ConfigManagerLoader;
import com.dianping.pigeon.remoting.provider.config.PoolConfigFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created by chenchongze on 16/10/15.
 */
public class PoolBeanDefinitionParser implements BeanDefinitionParser {

    /** Default placeholder prefix: "${" */
    public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";
    /** Default placeholder suffix: "}" */
    public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";
    private final Class<?> beanClass;
    private final boolean required;
    private static ConfigManager configManager = ConfigManagerLoader.getConfigManager();

    public PoolBeanDefinitionParser(Class<?> beanClass, boolean required) {
        this.beanClass = beanClass;
        this.required = required;
    }

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        return parse(element, parserContext, beanClass, required);
    }

    private BeanDefinition parse(Element element, ParserContext parserContext, Class<?> beanClass, boolean required) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setLazyInit(false);
        beanDefinition.setBeanClass(PoolBean.class);
        // 定义一个MutablePropertyValues包装xml文件中配置的所有属性，在初始化PoolBean时将这些属性依赖注入进具体Bean实例中
        MutablePropertyValues properties = beanDefinition.getPropertyValues();

        // 解析id属性值,映射到poolName
        String id = element.getAttribute("id");
        properties.addPropertyValue("poolName", id);

        // 解析corePoolSize属性值
        Integer corePoolSize = Integer.parseInt(resolveReference(element, "corePoolSize"));
        properties.addPropertyValue("corePoolSize", corePoolSize);
        String value = element.getAttribute("corePoolSize");
        if (value.startsWith(DEFAULT_PLACEHOLDER_PREFIX) && value.endsWith(DEFAULT_PLACEHOLDER_SUFFIX)) {
            PoolConfigFactory.getCoreSizeKeys().put(id, value.substring(2, value.length() - 1));
        }

        // 解析maxPoolSize属性值
        Integer maxPoolSize = Integer.parseInt(resolveReference(element, "maxPoolSize"));
        properties.addPropertyValue("maxPoolSize", maxPoolSize);
        value = element.getAttribute("maxPoolSize");
        if (value.startsWith(DEFAULT_PLACEHOLDER_PREFIX) && value.endsWith(DEFAULT_PLACEHOLDER_SUFFIX)) {
            PoolConfigFactory.getMaxSizeKeys().put(id, value.substring(2, value.length() - 1));
        }

        // 解析workQueueSize属性值
        Integer workQueueSize = Integer.parseInt(resolveReference(element, "workQueueSize"));
        properties.addPropertyValue("workQueueSize", workQueueSize);
        value = element.getAttribute("workQueueSize");
        if (value.startsWith(DEFAULT_PLACEHOLDER_PREFIX) && value.endsWith(DEFAULT_PLACEHOLDER_SUFFIX)) {
            PoolConfigFactory.getQueueSizeKeys().put(id, value.substring(2, value.length() - 1));
        }

        // 属性值校验
        if (corePoolSize < 0 ||
                maxPoolSize <= 0 ||
                maxPoolSize < corePoolSize ||
                workQueueSize <= 0)
            throw new IllegalArgumentException("please check pool config: " + id);

        //  注册到Spring容器BeanDefinition注册中心中
        parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);

        return beanDefinition;
    }
    private static String resolveReference(Element element, String attribute) {
        String value = element.getAttribute(attribute);
        if (value.startsWith(DEFAULT_PLACEHOLDER_PREFIX) && value.endsWith(DEFAULT_PLACEHOLDER_SUFFIX)) {
            String valueInCache = configManager.getStringValue(value.substring(2, value.length() - 1));
            if (valueInCache == null) {
                throw new IllegalStateException("undefined config property:" + element.getAttribute(attribute));
            } else {
                value = valueInCache;
            }
        }
        return value;
    }
}
