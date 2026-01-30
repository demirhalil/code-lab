package com.concurrency;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Demonstrates advanced locking using ReentrantLock.
 * ReentrantLock provides more flexibility than 'synchronized' (tryLock, fairness, etc.).
 */
public class LockingCounter implements Counter {
    private int count = 0;
    private final Lock lock = new ReentrantLock();

    @Override
    public void increment() {
        lock.lock();
        try {
            count++;
        } finally {
            // Always unlock in a finally block to avoid deadlocks in case of exceptions.
            lock.unlock();
        }
    }

    @Override
    public int getCount() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }
}
