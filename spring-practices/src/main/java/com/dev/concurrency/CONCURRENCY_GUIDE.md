# Concurrency in Spring Boot: High-Volume Inventory System

This module explores how Spring Boot handles concurrent requests and provides strategies to manage state safely in a multi-threaded environment.

## 1. The "Servlet" Mindset: Request-per-Thread
Spring Boot (via Tomcat) typically uses a **Thread Pool** to handle incoming HTTP requests.

- **Connector/Executor**: Tomcat's `Connector` listens for connections. It uses an `Executor` (thread pool) to assign a thread to each incoming request.
- **Request-per-Thread**: Each HTTP request is processed by a single thread from start to finish.
- **Pool Exhaustion**: When all threads in the pool are busy (e.g., waiting for a slow DB), new requests are queued. Once the queue is full, Tomcat starts rejecting connections (`Connection refused` or `503 Service Unavailable`).
- **Configuration**: Managed via `server.tomcat.threads.max` and `server.tomcat.threads.min-spare`.

## 2. Spring Bean Lifecycle & Thread Safety
- **Singleton (Default)**: Spring creates only one instance of `@RestController`, `@Service`, or `@Repository`. This single instance is shared across **all threads**.
    - **Implication**: These beans **must be stateless** or use **thread-safe** mechanisms to manage state. Avoid using simple instance variables (fields) to store request-specific data.
- **Prototype**: A new instance is created every time it is requested. Useful for stateful beans, but rarely used for Services/Controllers due to overhead.

## 3. State Management Strategies
In our `InventoryService`, we demonstrate four ways to protect the `stock` field:

### A. Synchronized Blocks
```java
public synchronized void reserveSynchronized(String productCode) { ... }
```
- **Pros**: Simple to implement, built into JVM.
- **Cons**: Coarse-grained. Serializes all requests, even for different products. Heavy performance hit under high load.

### B. ReentrantLock
```java
private final ReentrantLock lock = new ReentrantLock();
public void reserveWithLock(String productCode) {
    lock.lock();
    try { ... } finally { lock.unlock(); }
}
```
- **Pros**: More flexible than `synchronized`. Supports `tryLock()` (non-blocking attempt) and timeouts.
- **Cons**: Manual management required (must always unlock in `finally`). Still serializes access on a single JVM.

### C. Atomic Variables (`AtomicInteger`)
```java
private final AtomicInteger inMemoryStock = new AtomicInteger(100);
public int reserveAtomic() { return inMemoryStock.decrementAndGet(); }
```
- **Pros**: Extremely fast. Uses CPU-level Compare-And-Swap (CAS) instructions. Lock-free.
- **Cons**: Only works for single variables. Harder to use for complex logic involving multiple fields.

### D. Optimistic Locking (`@Version`)
```java
@Version
private Long version;
```
- **Pros**: Most scalable for distributed systems. Does not hold database locks. Detects conflicts at the database level.
- **Cons**: Requires handling `ObjectOptimisticLockingFailureException`. Best when conflicts are rare.

## 4. Modern Concurrency: @Async & CompletableFuture
Spring's `@Async` offloads tasks to a separate thread pool, freeing the main "Web" thread to handle more incoming requests.

- **ThreadPoolTaskExecutor**: A custom executor should be defined to prevent using the default `SimpleAsyncTaskExecutor` (which creates a new thread for every task).
- **CompletableFuture**: Allows for non-blocking asynchronous programming, enabling thread composition and better error handling.

## 5. Pitfalls to Avoid
1. **ThreadLocal Leaks**: Always clear `ThreadLocal` variables, especially when using thread pools, as threads are reused.
2. **Deadlocks**: Occur when two threads wait for each other to release locks. Always acquire locks in a consistent order.
3. **Blocking the Thread Pool**: Avoid performing long-running I/O operations inside the web thread pool. Use `@Async` or Reactive programming (WebFlux) for such tasks.
4. **False Sense of Security with `@Transactional`**: Transactions provide atomicity/isolation but **do not prevent race conditions** on their own unless using appropriate isolation levels (e.g., `SERIALIZABLE`) or locking.

## Analysis: Why Optimistic Locking?
For a **High-Volume Inventory System**, **Optimistic Locking** is generally the most efficient:
1. **Concurrency**: It allows multiple users to read the same data simultaneously without blocking.
2. **Scalability**: Unlike `synchronized` or `ReentrantLock`, it works across multiple instances of your application (distributed) because the check happens in the Database.
3. **Performance**: It avoids the overhead of managing locks until the very last moment (the update).
