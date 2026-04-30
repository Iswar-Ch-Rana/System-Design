package lld.Multithreading_And_Concurrency;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/// =================== Producer-Consumer Problem ===================

/// Classic synchronization problem:
/// - Producers put items into a shared buffer
/// - Consumers take items from the buffer
/// - Producer waits when buffer is full
/// - Consumer waits when buffer is empty
///
/// Solutions: wait/notify, BlockingQueue, Semaphores, Lock+Condition
///
/// =================== 1. wait/notify Approach ===================
///
/// PROS: ✓ Built-in ✓ Simple for single producer/consumer ✓ Fine-grained control
/// CONS: ✗ Error-prone ✗ Manual sync ✗ Must use while (not if) ✗ Hard to scale
///
/// Example: Coffee machine with buffer size = 1
class CoffeeMachine {
    private boolean isCoffeeReady = false;

    /// Producer: Make coffee
    public synchronized void makeCoffee() throws InterruptedException {
        while (isCoffeeReady) {
            wait(); /// Wait if coffee already ready
        }
        System.out.println("☕ Brewing coffee…");
        Thread.sleep(1000);
        isCoffeeReady = true;
        System.out.println("✓ Coffee ready!");
        notify();
    }

    /// Consumer: Drink coffee
    public synchronized void drinkCoffee() throws InterruptedException {
        while (!isCoffeeReady) {
            wait(); /// Wait if no coffee available
        }
        System.out.println("😋 Drinking coffee…");
        Thread.sleep(1000);
        isCoffeeReady = false;
        System.out.println("🍵 Cup empty!");
        notify();
    }
}

/// =================== 2. Bounded Buffer (Multiple Producers/Consumers) ===================

/// PROS: ✓ Multiple producers/consumers ✓ Bounded buffer ✓ Memory safe
/// CONS: ✗ notifyAll() overhead ✗ Manual sync ✗ Hard to debug


/// Data item: Code submission
class Submission {
    private static int idCounter = 1;
    private final int submissionId;
    private final String userName;

    public Submission(String userName) {
        this.userName = userName;
        this.submissionId = idCounter++;
    }

    public int getSubmissionId() {
        return submissionId;
    }

    public String getUserName() {
        return userName;
    }
}

/// Bounded queue with wait/notify
class SubmissionQueue {
    private final Queue<Submission> queue = new LinkedList<>();
    private final int MAX_CAPACITY = 5;

    /// Producer: Submit code
    public synchronized void submit(Submission submission) throws InterruptedException {
        while (queue.size() == MAX_CAPACITY) {
            System.out.println("⏳ Queue full. " + submission.getUserName() + " waiting...");
            wait();
        }
        queue.offer(submission);
        System.out.println("📝 " + submission.getUserName() + " submitted #" + submission.getSubmissionId());
        notifyAll();
    }

    /// Consumer: Process submission
    public synchronized Submission consume(String judgeName) throws InterruptedException {
        while (queue.isEmpty()) {
            System.out.println("⏸ " + judgeName + " waiting...");
            wait();
        }
        Submission sub = queue.poll();
        System.out.println("⚙️ " + judgeName + " evaluating #" + sub.getSubmissionId());
        notifyAll();
        return sub;
    }
}

/// =================== 3. BlockingQueue (Recommended) ===================

/// PROS: ✓ Thread-safe ✓ Built-in blocking ✓ Clean code ✓ Production-ready
/// CONS: ✗ Less control ✗ Higher abstraction

/// Modern approach using BlockingQueue
class ModernSubmissionQueue {
    private final BlockingQueue<Submission> queue = new LinkedBlockingQueue<>(5);

    public void submit(Submission submission) throws InterruptedException {
        queue.put(submission); /// Auto-blocks if full
        System.out.println("📝 " + submission.getUserName() + " submitted #" + submission.getSubmissionId());
    }

    public Submission consume(String judgeName) throws InterruptedException {
        Submission sub = queue.take(); /// Auto-blocks if empty
        System.out.println("⚙️ " + judgeName + " evaluating #" + sub.getSubmissionId());
        return sub;
    }
}


/// =================== Comparison ===================

/// Approach         | Complexity | Thread-Safe | Best For
/// -----------------|------------|-------------|---------------------------
/// wait/notify      | High       | Manual      | Learning
/// Bounded Buffer   | High       | Manual      | Multiple producers/consumers
/// BlockingQueue    | Low        | Built-in    | Production (Recommended)
///
/// Key Points:
/// 1. Use 'while' not 'if' for wait conditions
/// 2. Use notifyAll() for multiple threads
/// 3. Prefer BlockingQueue for production
///
/// =================== Main ===================

public class ProducerConsumerProblem {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Demo 1: Coffee Machine ===\n");
        demoCoffeeMachine();
        Thread.sleep(3000);

        System.out.println("\n=== Demo 2: Submission Queue ===\n");
        demoSubmissionQueue();
        Thread.sleep(5000);

        System.out.println("\n=== Demo 3: BlockingQueue ===\n");
        demoBlockingQueue();
        Thread.sleep(5000);

        System.exit(0);
    }

    private static void demoCoffeeMachine() {
        CoffeeMachine machine = new CoffeeMachine();
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) machine.makeCoffee();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) machine.drinkCoffee();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        producer.start();
        consumer.start();
    }

    private static void demoSubmissionQueue() {
        SubmissionQueue queue = new SubmissionQueue();
        Thread user1 = new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    queue.submit(new Submission("Alice"));
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        Thread user2 = new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    queue.submit(new Submission("Bob"));
                    Thread.sleep(700);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        Thread judge1 = new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    queue.consume("Judge-1");
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        Thread judge2 = new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    queue.consume("Judge-2");
                    Thread.sleep(1200);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        user1.start();
        user2.start();
        judge1.start();
        judge2.start();
    }

    private static void demoBlockingQueue() {
        ModernSubmissionQueue queue = new ModernSubmissionQueue();
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    queue.submit(new Submission("User-" + (i + 1)));
                    Thread.sleep(300);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    queue.consume("Judge");
                    Thread.sleep(800);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        producer.start();
        consumer.start();
    }
}

