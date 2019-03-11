package com.rest.dao.cache;

/**
 * Simple wrapper interface.
 *
 * @param <K>
 * @param <V>
 */
public abstract interface CacheWrapper<K, V> {
	
	public abstract void put(K key, V value);
	public abstract V get(K key);
	public abstract boolean containsKey(K key);
	public abstract void remove(K key);
	public abstract void clear();
}
