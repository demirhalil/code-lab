package com.concurrency;

import com.concurrency.Counter;

/**
 * Demonstrates basic protection using the 'synchronized' keyword.
 * Only one thread can execute an increment() or getCount() at a time on the same instance.
 */
public class SynchronizedCounter implements Counter {
    private int count = 0;

    @Override
    public synchronized void increment() {
        count++;
    }

    @Override
    public synchronized int getCount() {
        return count;
    }
}
