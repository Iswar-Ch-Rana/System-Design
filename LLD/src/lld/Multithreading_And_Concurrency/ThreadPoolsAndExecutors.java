package lld.Multithreading_And_Concurrency;

import java.util.concurrent.*;

// ========================== 1. Fixed Thread Pool ==========================

/**
 * Task that simulates sending an email.
 * Implements Runnable as it does not return a result.
 */
class EmailTask implements Runnable {
    private final String recipient;

    public EmailTask(String recipient) {
        this.recipient = recipient;
    }

    @Override
    public void run() {
        System.out.println("Sending email to " + recipient + " on " + Thread.currentThread().getName());
        try {
            // Simulate network delay
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Email sent to " + recipient);
    }
}

// ========================== 2. Future & Callable ==========================

/**
 * Task that performs a calculation and returns a result.
 * Implements Callable to allow returning a value and throwing checked exceptions.
 */
class CalculationTask implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        System.out.println("Performing calculation on " + Thread.currentThread().getName());
        Thread.sleep(2000);
        return 77;
    }
}

// ====================== Main Entry Point ===================================

/**
 * Demonstrates various ExecutorService implementations and Thread Pool patterns.
 */
public class ThreadPoolsAndExecutors {

    public static void main(String[] args) {

        // ---------- Demo 1: Fixed Thread Pool ----------
        System.out.println("=== 1. Fixed Thread Pool Demo ===\n");
        demoFixedThreadPool();

        // ---------- Demo 2: Cached Thread Pool ----------
        System.out.println("\n=== 2. Cached Thread Pool Demo ===\n");
        demoCachedThreadPool();

        // ---------- Demo 3: Single Thread Executor ----------
        System.out.println("\n=== 3. Single Thread Executor Demo ===\n");
        demoSingleThreadExecutor();

        // ---------- Demo 4: Future & Submit ----------
        System.out.println("\n=== 4. Future & Submit (Callable) Demo ===\n");
        demoFutureSubmit();

        // ---------- Demo 5: Scheduled Thread Pool ----------
        System.out.println("\n=== 5. Scheduled Thread Pool Demo ===\n");
        demoScheduledThreadPool();
    }

    /**
     * FixedThreadPool: Reuses a fixed number of threads.
     * Ideal when you know the load and want to limit resource usage.
     */
    private static void demoFixedThreadPool() {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 15; i++) {
            executor.execute(new EmailTask("user" + i + "@gmail.com"));
        }

        executor.shutdown();
        try {
            // Wait for tasks to complete
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
        System.out.println("Fixed Thread Pool tasks submitted.");
    }

    /**
     * CachedThreadPool: Creates new threads as needed, but reuses idle threads.
     * Ideal for many short-lived asynchronous tasks.
     */
    private static void demoCachedThreadPool() {
        ExecutorService executor = Executors.newCachedThreadPool();

        executor.execute(() -> System.out.println("Cached Task A running on: " + Thread.currentThread().getName()));
        executor.execute(() -> System.out.println("Cached Task B running on: " + Thread.currentThread().getName()));

        executor.shutdown();
    }

    /**
     * SingleThreadExecutor: Uses a single worker thread.
     * Ensures tasks are executed sequentially (FIFO).
     */
    private static void demoSingleThreadExecutor() {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> System.out.println("Single Thread Task 1 (Sequential)"));
        executor.execute(() -> System.out.println("Single Thread Task 2 (Sequential)"));

        executor.shutdown();
    }

    /**
     * Future & Submit: Demonstrates retrieving results from asynchronous tasks.
     * submit() accepts Callable and returns a Future.
     */
    private static void demoFutureSubmit() {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Future<Integer> futureResult = executor.submit(new CalculationTask());

        System.out.println("Main thread is free to do other work...");

        try {
            // get() is a blocking call that waits for the task to finish
            Integer result = futureResult.get();
            System.out.println("Result received from Future: " + result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        executor.shutdown();
    }

    /**
     * ScheduledThreadPool: Can schedule commands to run after a delay or periodically.
     */
    private static void demoScheduledThreadPool() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        System.out.println("Scheduling task to run after 3 seconds...");

        scheduler.schedule(() -> {
            System.out.println("Delayed task executed! " + Thread.currentThread().getName());
        }, 3, TimeUnit.SECONDS);

        scheduler.shutdown();
    }
}
