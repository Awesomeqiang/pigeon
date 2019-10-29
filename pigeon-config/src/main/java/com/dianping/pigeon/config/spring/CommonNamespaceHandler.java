/**
 * Dianping.com Inc.
 * Copyright (c) 2003-2013 All Rights Reserved.
 */
package com.dianping.pigeon.config.spring;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import com.dianping.pigeon.extension.ExtensionLoader;

public class CommonNamespaceHandler extends NamespaceHandlerSupport {

	public void init() {
        /**
         * SPI 全称为 (Service Provider Interface) ,是JDK（1.5开始）内置的一种服务提供发现机制。
         */
		// 加载类路径 META-INF/services目录下的com.dianping.pigeon.config.spring.BeanDefinitionParserLoader文件中定义的所有loader
		List<BeanDefinitionParserLoader> loaders = ExtensionLoader.getExtensionList(BeanDefinitionParserLoader.class);
		if (loaders != null) {
			for (BeanDefinitionParserLoader loader : loaders) {
				// 调用每个loader的loadBeanDefinitionParsers方法获取每个loader负责的解析器
				Map<String, BeanDefinitionParser> parsers = loader.loadBeanDefinitionParsers();
				if (parsers != null) {
					for (String key : parsers.keySet()) {
						// 将解析器注册到BeanDefinitionParser中，在调用parse函数中，根据标签找到相应的解析器进行解析元素
						registerBeanDefinitionParser(key, parsers.get(key));
					}
				}
			}
		}
	}

}