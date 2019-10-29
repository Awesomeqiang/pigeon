/**
 * Dianping.com Inc.
 * Copyright (c) 2003-2013 All Rights Reserved.
 */
package com.dianping.pigeon.remoting.common.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.xml.BeanDefinitionParser;

import com.dianping.pigeon.config.spring.BeanDefinitionParserLoader;

/**
 * 
 * @author xiangwu
 */
public class AnnotationBeanDefinitionParserLoader implements BeanDefinitionParserLoader {

	@Override
	public Map<String, BeanDefinitionParser> loadBeanDefinitionParsers() {
		Map<String, BeanDefinitionParser> parsers = new HashMap<String, BeanDefinitionParser>();
		// 初始化AnnotationBeanDefinitionParser,加入集合中
		parsers.put("annotation", new AnnotationBeanDefinitionParser(AnnotationBean.class, false));
		return parsers;
	}

}