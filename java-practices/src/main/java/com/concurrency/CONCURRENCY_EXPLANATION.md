# Java Multithreading & Concurrency

This example demonstrates key multithreading concepts in Java, located in `com.architecture.concurrency`.

## Key Concepts Explained

### 1. Shared Resource vs. Instance Resource
*   **Shared Resource**: An object or variable accessed by multiple threads. In our demo, a single `Counter` instance shared among 10 threads is a shared resource. Without protection, this leads to **Race Conditions**.
*   **Instance Resource**: When each thread has its own private instance of a resource. Since memory is not shared, no synchronization is needed.

### 2. Race Condition
A race condition occurs when multiple threads attempt to modify a shared resource simultaneously. For example, `count++` is not atomic; it consists of:
1.  **Read** the current value.
2.  **Increment** the value.
3.  **Write** the new value back.
If two threads read the same value before either writes back, one increment is lost.

### 3. Protection & Locking Mechanisms

#### Basic: `synchronized` Keyword
Uses Java's intrinsic locks (monitor locks). It's the simplest way to protect a method or a block of code.
*   **Pros**: Easy to use, built-in.
*   **Cons**: Less flexible (cannot "try" to lock, no timeout, always reentrant).

#### Advanced: `ReentrantLock`
A manual locking mechanism from `java.util.concurrent.locks`.
*   **Pros**: More control. Supports `tryLock()`, timeouts, and fairness policies.
*   **Cons**: Requires careful use (must unlock in a `finally` block).

#### Modern: Atomic Classes (CAS)
Uses `AtomicInteger`, `AtomicLong`, etc. from `java.util.concurrent.atomic`.
*   **Mechanism**: Uses **Compare-And-Swap (CAS)**, a low-level CPU instruction that updates a value only if it hasn't changed since it was read.
*   **Pros**: Lock-free, very high performance for simple operations.

## How to Run the Demo

You can run the demonstration class `com.architecture.concurrency.ConcurrencyDemo` to see these concepts in action and observe the race conditions being fixed by different mechanisms.
