# Deep Dive: Shared Resources & Memory Model

To understand why a simple `private int count` in an object can be a "shared resource", we need to look at how Java manages memory and how threads interact with it.

## 1. Heap vs. Stack Memory

In Java, memory is divided into two main areas: **Stack** and **Heap**.

### The Stack (Private to each Thread)
- Every thread has its own private **Stack**.
- It stores local variables (primitives and object references) and method call frames.
- **No thread can access another thread's stack.**
- Variables here are inherently thread-safe because they aren't shared.

### The Heap (Shared by all Threads)
- All objects (instances of classes) live on the **Heap**.
- When you do `new UnsafeCounter()`, that object is allocated on the Heap.
- **Multiple threads can have a reference to the same object on the Heap.**

## 2. Why is `count` a Shared Resource?

Even though `count` is an instance field (not `static`), it lives inside the object on the **Heap**.

If you create ONE instance of a class and pass it to multiple threads, they all point to the SAME memory address on the Heap.

### The Scenario:
```java
Counter sharedCounter = new UnsafeCounter(); // One object on the Heap

// Thread A and Thread B both get a reference to 'sharedCounter'
Thread A -> [ sharedCounter (Heap Address: 0x123) ]
Thread B -> [ sharedCounter (Heap Address: 0x123) ]
```

When Thread A executes `sharedCounter.increment()`, it looks at the memory at `0x123`, reads the `count` field, and tries to update it. At the same time, Thread B is doing the exact same thing to the exact same memory location. This is the definition of a **Shared Resource**.

## 3. Real-World Production Examples

In production systems, we rarely just increment counters for fun. Here are real scenarios where this matters:

### A. Thread-Safe Cache (Service Level)
Imagine a `UserService` that caches user profiles in a `HashMap`. Since many web requests (each in its own thread) might try to access or update the cache simultaneously, the `HashMap` becomes a shared resource.
- **Solution**: Use `ConcurrentHashMap` or explicit locks.

### B. Connection Pools
A database connection pool is a shared resource. Multiple threads "borrow" connections from the pool. The pool's internal list of available connections must be protected so two threads don't get the same connection.

### C. Rate Limiting (API Gateway)
An API Gateway might track how many requests a user has made in the last minute. This counter (often stored in memory or Redis) is shared across all threads handling that user's requests.

---

## 4. Production-Ready Tool: `ConcurrentHashMap` vs `HashMap`

Instead of manually locking everything, Java provides "Concurrent Collections" which are optimized for high-performance sharing.

| Tool | Usage | Why it's production-ready |
| :--- | :--- | :--- |
| `ConcurrentHashMap` | Caching, lookups | Uses "Lock Stripping" (only locks a part of the map, not the whole thing). |
| `CopyOnWriteArrayList` | List of listeners/observers | Great for "Read-Heavy" scenarios (reads are lock-free). |
| `AtomicLong` | Metrics, IDs | High performance, no thread suspension. |
