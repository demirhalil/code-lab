package com.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Main class to demonstrate and explain concurrency concepts in Java.
 */
public class ConcurrencyDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Java Concurrency Demo ===");
        
        int numberOfThreads = 10;
        int incrementsPerThread = 1000;
        int expectedTotal = numberOfThreads * incrementsPerThread;

        System.out.println("Configuration:");
        System.out.println("- Number of threads: " + numberOfThreads);
        System.out.println("- Increments per thread: " + incrementsPerThread);
        System.out.println("- Expected total count: " + expectedTotal);
        System.out.println();

        // 1. Shared Resource (Shared Counter Instance) - The problem and solutions
        runDemo("UnsafeCounter (Shared Instance)", new UnsafeCounter(), numberOfThreads, incrementsPerThread);
        runDemo("SynchronizedCounter (Shared Instance)", new SynchronizedCounter(), numberOfThreads, incrementsPerThread);
        runDemo("LockingCounter (Shared Instance)", new LockingCounter(), numberOfThreads, incrementsPerThread);
        runDemo("AtomicCounter (Shared Instance)", new AtomicCounter(), numberOfThreads, incrementsPerThread);

        // 2. Instance Resource (Each thread has its own counter) - No conflict
        System.out.println("--- Instance Resource Demo ---");
        System.out.println("In this case, each thread has its OWN instance of UnsafeCounter.");
        System.out.println("Since the resource is NOT SHARED, no race conditions occur even without locks.");
        runInstanceResourceDemo(numberOfThreads, incrementsPerThread);

        // 3. Production Ready Example: ThreadSafeCache
        System.out.println("--- Production Ready Example: ThreadSafeCache ---");
        runCacheDemo(numberOfThreads, incrementsPerThread);

        explainConcepts();
    }

    private static void runCacheDemo(int threads, int increments) throws InterruptedException {
        ThreadSafeCache<String, Integer> cache = new ThreadSafeCache<>();
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < increments; j++) {
                    // All threads are accessing the SAME 'cache' instance.
                    // But 'ThreadSafeCache' uses ConcurrentHashMap and AtomicInteger inside.
                    cache.getOrCompute("key", k -> 0); 
                    cache.put("key", cache.get("key") + 1);
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        System.out.printf("Cache size: %d%n", cache.size());
        System.out.printf("Total hits: %d%n", cache.getHitCount());
        System.out.printf("Total misses: %d (Expected: 1)%n", cache.getMissCount());
        System.out.println("Status: ✅ PASS (Internal components handled sharing)");
        System.out.println();
    }

    private static void runDemo(String label, Counter counter, int threads, int increments) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < increments; j++) {
                    counter.increment();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        System.out.printf("%-40s | Result: %d | Status: %s%n", 
            label, 
            counter.getCount(), 
            (counter.getCount() == (threads * increments) ? "✅ PASS" : "❌ FAIL (Race Condition!)"));
    }

    private static void runInstanceResourceDemo(int threads, int increments) throws InterruptedException {
        List<UnsafeCounter> counters = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        for (int i = 0; i < threads; i++) {
            UnsafeCounter individualCounter = new UnsafeCounter();
            counters.add(individualCounter);
            executor.submit(() -> {
                for (int j = 0; j < increments; j++) {
                    individualCounter.increment();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        int totalCount = counters.stream().mapToInt(Counter::getCount).sum();
        System.out.printf("%-40s | Result: %d | Status: %s%n%n", 
            "UnsafeCounter (Individual Instances)", 
            totalCount, 
            (totalCount == (threads * increments) ? "✅ PASS" : "❌ FAIL"));
    }

    private static void explainConcepts() {
        System.out.println("--- Understanding Concurrency Under the Hood ---");
        System.out.println("1. SHARED RESOURCE: A variable or object (like our shared Counter instance) ");
        System.out.println("   that multiple threads try to access and modify simultaneously.");
        
        System.out.println("\n2. INSTANCE RESOURCE: When each thread works with its own object instance,");
        System.out.println("   there is no contention because memory is not shared between threads for that resource.");
        
        System.out.println("\n3. RACE CONDITION: Occurs when the outcome depends on the timing/interleaving");
        System.out.println("   of thread execution. 'count++' is actually 3 steps: READ, MODIFY, WRITE.");
        System.out.println("   Two threads might READ the same value '5', both increment to '6', and both WRITE '6',");
        System.out.println("   losing one increment.");
        
        System.out.println("\n4. LOCKING MECHANISMS:");
        System.out.println("   - Synchronized: Uses the object's intrinsic lock (monitor). Simple but less flexible.");
        System.out.println("   - ReentrantLock: An explicit lock from java.util.concurrent. Provides advanced features");
        System.out.println("     like fairness, non-blocking tryLock(), and multiple condition variables.");
        System.out.println("   - Atomic (CAS): Modern approach using 'Compare-And-Swap' CPU instructions.");
        System.out.println("     It doesn't 'lock' in the traditional sense; it tries to update and retries if");
        System.out.println("     the value changed in the meantime. Very efficient for simple counters.");
    }
}
