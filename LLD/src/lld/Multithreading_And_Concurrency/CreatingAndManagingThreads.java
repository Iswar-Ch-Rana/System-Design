package lld.Multithreading_And_Concurrency;

import java.util.concurrent.*;

// ========================== 1. Sequential Approach ==========================

/**
 * Demonstrates sequential execution of tasks (SMS, Email, ETA).
 * Total time = sum of all individual task delays.
 */
class OrderService {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Sequential Execution ===\n");
        System.out.println("Placing order...\n");

        long startTime = System.currentTimeMillis();

        sendSMS();
        System.out.println("Task 1 done.\n");

        sendEmail();
        System.out.println("Task 2 done.\n");

        String eta = calculateETA();
        System.out.println("Order placed. Estimated Time of Arrival: " + eta);
        System.out.println("Task 3 done.\n");

        long endTime = System.currentTimeMillis();
        System.out.println("Total time taken (sequential): " + (endTime - startTime) + " ms\n");
    }

    private static void sendSMS() {
        try {
            Thread.sleep(2000);
            System.out.println("SMS Sent!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void sendEmail() {
        try {
            Thread.sleep(3000);
            System.out.println("Email Sent!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static String calculateETA() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "25 minutes";
    }
}

// =================== 2. Multithreading with Thread Class ====================

/**
 * Sends SMS by extending the Thread class.
 */
class SMSThread extends Thread {
    @Override
    public void run() {
        try {
            Thread.sleep(2000);
            System.out.println("SMS Sent!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

/**
 * Sends Email by extending the Thread class.
 */
class EmailThread extends Thread {
    @Override
    public void run() {
        try {
            Thread.sleep(3000);
            System.out.println("Email Sent!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

// =================== 3. Multithreading with Callable =======================

/**
 * Calculates ETA using Callable (returns a result).
 */
class ETACalculator implements Callable<String> {
    private final String location;

    public ETACalculator(String location) {
        this.location = location;
    }

    @Override
    public String call() throws Exception {
        Thread.sleep(5000);
        return "ETA for " + location + ": 25 minutes";
    }
}

// =============== 4. Runnable & Callable with FutureTask ====================

/**
 * SMS task implementing Runnable (no return value).
 */
class SMSTask implements Runnable {
    @Override
    public void run() {
        try {
            Thread.sleep(2000);
            System.out.println("SMS Sent using Runnable.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

/**
 * Email task implementing Runnable (no return value).
 */
class EmailTask implements Runnable {
    @Override
    public void run() {
        try {
            Thread.sleep(3000);
            System.out.println("Email Sent using Runnable.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

/**
 * ETA calculation task implementing Callable (returns a result).
 */
class ETACalculationTask implements Callable<String> {
    @Override
    public String call() throws InterruptedException {
        Thread.sleep(5000);
        System.out.println("ETA calculated using Callable.");
        return "ETA: 25 minutes";
    }
}

// ====================== Main Entry Point ===================================

public class CreatingAndManagingThreads {

    public static void main(String[] args) {

        // ---------- Demo 1: Sequential Execution ----------
        try {
            OrderService.main(args);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // ---------- Demo 2: Thread Class (extends Thread) ----------
        System.out.println("=== Multithreading with Thread Class ===\n");
        demoWithThreadClass();

        // ---------- Demo 3: Callable + FutureTask ----------
        System.out.println("\n=== Callable + FutureTask ===\n");
        demoWithCallable();

        // ---------- Demo 4: Lambda Runnable ----------
        System.out.println("\n=== Lambda Runnable ===\n");
        demoWithLambda();

        // ---------- Demo 5: Runnable + Callable with manual Threads ----------
        System.out.println("\n=== Runnable + Callable with Manual Threads ===\n");
        demoWithManualThreads();

        // ---------- Demo 6: ExecutorService ----------
        System.out.println("\n=== ExecutorService ===\n");
        demoWithExecutorService();
    }

    // ---- Demo 2: Using Thread subclass ----
    private static void demoWithThreadClass() {
        SMSThread smsThread = new SMSThread();
        EmailThread emailThread = new EmailThread();

        long startTime = System.currentTimeMillis();

        smsThread.start();
        emailThread.start();

        try {
            smsThread.join();
            emailThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Total time taken (Thread class): " + (endTime - startTime) + " ms\n");
    }

    // ---- Demo 3: Using Callable + FutureTask ----
    private static void demoWithCallable() {
        FutureTask<String> futureTask = new FutureTask<>(new ETACalculator("IND"));
        Thread thread = new Thread(futureTask);
        thread.start();

        try {
            String result = futureTask.get();
            System.out.println(result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    // ---- Demo 4: Using Lambda as Runnable ----
    private static void demoWithLambda() {
        Runnable task = () -> {
            try {
                Thread.sleep(2000);
                System.out.println("Task completed using Lambda expression.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Thread thread = new Thread(task);
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // ---- Demo 5: Runnable + Callable with manual Thread management ----
    private static void demoWithManualThreads() {
        FutureTask<String> etaTask = new FutureTask<>(new ETACalculationTask());
        Thread etaThread = new Thread(etaTask);
        Thread smsThread = new Thread(new SMSTask());
        Thread emailThread = new Thread(new EmailTask());

        long startTime = System.currentTimeMillis();

        etaThread.start();
        smsThread.start();
        emailThread.start();

        try {
            System.out.println(etaTask.get());
            smsThread.join();
            emailThread.join();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("All tasks completed in " + (endTime - startTime) + " ms\n");
    }

    // ---- Demo 6: Using ExecutorService for thread pool management ----
    private static void demoWithExecutorService() {
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        Future<String> etaResult = executorService.submit(new ETACalculationTask());
        executorService.submit(new SMSTask());
        executorService.submit(new EmailTask());

        try {
            System.out.println(etaResult.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        executorService.shutdown();
        System.out.println("ExecutorService shut down. All tasks completed.\n");
    }
}
