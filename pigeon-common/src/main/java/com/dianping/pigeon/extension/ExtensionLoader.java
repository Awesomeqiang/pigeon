/**
 * Dianping.com Inc.
 * Copyright (c) 2003-2013 All Rights Reserved.
 */
package com.dianping.pigeon.extension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author xiangwu
 * @Sep 11, 2013
 * 
 */
public final class ExtensionLoader {

	private static Map<Class<?>, Object> extensionMap = new ConcurrentHashMap<Class<?>, Object>();

	private static Map<Class<?>, List<?>> extensionListMap = new ConcurrentHashMap<Class<?>, List<?>>();

	private ExtensionLoader() {
	}

	public static <T> T getExtension(Class<T> clazz) {
		T extension = (T) extensionMap.get(clazz);
		if (extension == null) {
			extension = newExtension(clazz);
			if (extension != null) {
				extensionMap.put(clazz, extension);
			}
		}
		return extension;
	}

	public static <T> List<T> getExtensionList(Class<T> clazz) {
		List<T> extensions = (List<T>) extensionListMap.get(clazz);
		if (extensions == null) {
			extensions = newExtensionList(clazz);
			if (!extensions.isEmpty()) {
				extensionListMap.put(clazz, extensions);
			}
		}
		return extensions;
	}

	public static <T> T newExtension(Class<T> clazz) {
		ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz);
		for (T service : serviceLoader) {
			return service;
		}
		return null;
	}

	public static <T> List<T> newExtensionList(Class<T> clazz) {
		// 在ServiceLoader.load的时候，根据传入的接口类，
		// 遍历META-INF/services目录下的以该类命名的文件中的所有类，并实例化返回。
		ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz);
		List<T> extensions = new ArrayList<T>();
		for (T service : serviceLoader) {
			extensions.add(service);
		}
		return extensions;
	}
}
