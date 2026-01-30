package com.concurrency;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A production-ready example of a thread-safe Cache with hit counting.
 * This demonstrates:
 * 1. ConcurrentHashMap for high-concurrency storage.
 * 2. AtomicInteger for thread-safe metrics.
 * 3. computeIfAbsent for atomic check-and-act operations.
 */
public class ThreadSafeCache<K, V> {
    
    // The main data store - thread-safe by design
    private final Map<K, V> cache = new ConcurrentHashMap<>();
    
    // Shared resource for metrics
    private final AtomicInteger hits = new AtomicInteger(0);
    private final AtomicInteger misses = new AtomicInteger(0);

    public V get(K key) {
        V value = cache.get(key);
        if (value != null) {
            hits.incrementAndGet(); // Thread-safe increment
        } else {
            misses.incrementAndGet(); // Thread-safe increment
        }
        return value;
    }

    public void put(K key, V value) {
        // ConcurrentHashMap.put is thread-safe
        cache.put(key, value);
    }

    /**
     * Demonstrates a common production pattern: 
     * Get from cache, or if not present, compute it and put it in cache.
     * All done atomically for that key.
     */
    public V getOrCompute(K key, java.util.function.Function<K, V> computer) {
        return cache.computeIfAbsent(key, k -> {
            misses.incrementAndGet();
            return computer.apply(k);
        });
    }

    public int getHitCount() {
        return hits.get();
    }

    public int getMissCount() {
        return misses.get();
    }

    public int size() {
        return cache.size();
    }
}
