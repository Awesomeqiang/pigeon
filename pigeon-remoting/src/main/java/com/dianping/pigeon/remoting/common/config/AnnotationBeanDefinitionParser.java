/**
 * Dianping.com Inc.
 * Copyright (c) 2003-2013 All Rights Reserved.
 */
package com.dianping.pigeon.remoting.common.config;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import com.dianping.pigeon.log.Logger;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.dianping.pigeon.config.ConfigManager;
import com.dianping.pigeon.config.ConfigManagerLoader;
import com.dianping.pigeon.log.LoggerLoader;

public class AnnotationBeanDefinitionParser implements BeanDefinitionParser {

	/** Default placeholder prefix: "${" */
	public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";
	/** Default placeholder suffix: "}" */
	public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";

	private static final Logger logger = LoggerLoader.getLogger(AnnotationBeanDefinitionParser.class);

	private final Class<?> beanClass;

	private final boolean required;

	private static AtomicInteger idCounter = new AtomicInteger();

	private static ConfigManager configManager = ConfigManagerLoader.getConfigManager();

	public AnnotationBeanDefinitionParser(Class<?> beanClass, boolean required) {
		this.beanClass = beanClass;
		this.required = required;
	}

	public BeanDefinition parse(Element element, ParserContext parserContext) {
		return parse(element, parserContext, beanClass, required);
	}

	//解析配置
	private static BeanDefinition parse(Element element, ParserContext parserContext, Class<?> beanClass,
			boolean required) {
		//创建一个RootBeanDefinition
		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		//非懒加载
		beanDefinition.setLazyInit(false);
		String id = element.getAttribute("id");
		if (StringUtils.isBlank(id)) {
			id = "pigeonAnnotation_" + idCounter.incrementAndGet();
		}
		// 设置beanDefinition实例化时，具体的实现类是AnnotationBean
		beanDefinition.setBeanClass(AnnotationBean.class);

		// 获取xml配置的属性值，这里主要获取package属性，用来定义扫描的包空间
		MutablePropertyValues properties = beanDefinition.getPropertyValues();
		if (element.hasAttribute("package")) {
			properties.addPropertyValue("package", resolveReference(element, "package"));
		}

		// 注册当前BeanDefinition到Spring容器BeanDefinition注册中心，
        // 在后续初始化Spring容器Bean的时候，会初始化当前BeanDefinition对应的实例类AnnotationBean。
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