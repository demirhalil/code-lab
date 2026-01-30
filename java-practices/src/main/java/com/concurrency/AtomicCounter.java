package com.concurrency;


import java.util.concurrent.atomic.AtomicInteger;

/**
 * Demonstrates modern lock-free concurrency using Atomic classes.
 * These use low-level CPU instructions like Compare-And-Swap (CAS) to ensure atomicity
 * without traditional locking overhead.
 */
public class AtomicCounter implements Counter {
    private final AtomicInteger count = new AtomicInteger(0);

    @Override
    public void increment() {
        count.incrementAndGet();
    }

    @Override
    public int getCount() {
        return count.get();
    }
}
