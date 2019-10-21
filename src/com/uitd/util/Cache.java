package com.uitd.util;

import com.whirlycott.cache.CacheException;
import com.whirlycott.cache.CacheManager;

public class Cache {
	private static com.whirlycott.cache.Cache cache = null;

	/**
	 * 存入缓存
	 * 
	 * @param name
	 * @param obj
	 */
	public static void put(String name, Object obj) {
		if (cache == null) {
			try {
				cache = CacheManager.getInstance().getCache();
				cache.store(name, obj);
			} catch (CacheException e) {
				e.printStackTrace();
			}
		} else {
			cache.store(name, obj);
		}
	}

	/**
	 * 存入缓存
	 * 
	 * @param name
	 * @param obj
	 * @param expire
	 *            超时时间（单位秒）
	 */
	public static void put(String name, Object obj, long expire) {
		if (cache == null) {
			try {
				cache = CacheManager.getInstance().getCache();
				cache.store(name, obj, expire * 1000);
			} catch (CacheException e) {
				e.printStackTrace();
			}
		} else {
			cache.store(name, obj, expire * 1000);
		}
	}

	/**
	 * 取出缓存
	 * 
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T get(String name) {
		if (cache != null) {
			return (T) cache.retrieve(name);
		}
		return null;
	}
}