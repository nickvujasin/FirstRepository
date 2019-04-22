package com.rest.dao.cache;

import javax.cache.Cache;
import javax.cache.CacheManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JCacheWrapper<K, V> implements CacheWrapper<K, V> {
	
	private static final Logger LOG = LogManager.getLogger(JCacheWrapper.class);
	
	private final String cacheName;
	private final CacheManager cacheManager;

	public JCacheWrapper(String cacheName, CacheManager cacheManager) {
		this.cacheName = cacheName;
		this.cacheManager = cacheManager;
		LOG.info("Creating cache {} using cache manager {}.", cacheName, cacheManager.getClass().getName());
	}

	public void put(K key, V value) {
		getCache().put(key, value);
	}
	
	public boolean putIfAbsent(K key, V value) {
		return getCache().putIfAbsent(key, value);
	}

	public V get(K key) {
		Object value = getCache().get(key);
		if (value != null) {
			return (V) value;
		}
		return null;
	}
	
	public boolean containsKey(K key) {
		return getCache().containsKey(key);
	}
	
	public void remove(K key) {
		getCache().remove(key);
	}

	public void clear() {
		getCache().clear();
	}
	
	public Cache<Object, Object> getCache() {
		return cacheManager.getCache(cacheName, Object.class, Object.class);
	}
}
