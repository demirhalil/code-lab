package com.concurrency;


/**
 * Demonstrates a thread-unsafe counter.
 * This class has a shared resource (the 'count' variable).
 * When multiple threads access it without protection, a race condition occurs.
 */
public class UnsafeCounter implements Counter {
    private int count = 0;

    @Override
    public void increment() {
        // This is not atomic: it involves read, update, and write.
        // Multiple threads can read the same value before any of them write back.
        count++;
    }

    @Override
    public int getCount() {
        return count;
    }
}
