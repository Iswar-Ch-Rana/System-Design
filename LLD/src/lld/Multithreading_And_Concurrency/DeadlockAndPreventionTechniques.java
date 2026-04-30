package lld.Multithreading_And_Concurrency;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/// =================== What is Deadlock? ===================

/// Deadlock occurs when two or more threads are blocked forever, waiting for each other.
///
/// Four Coffman Conditions (ALL must be true for deadlock):
/// 1. Mutual Exclusion: Only one thread can hold a resource at a time
/// 2. Hold and Wait: Thread holds one resource while waiting for another
/// 3. No Preemption: Resources cannot be forcibly taken from threads
/// 4. Circular Wait: Threads form a circular chain, each waiting for the next
///
/// Breaking ANY ONE of these conditions prevents deadlock!

// =================== 1. Deadlock Scenario ===================

// Simple bank account with synchronized methods.
// Used to demonstrate deadlock when transferring between accounts.
class BankAccount {
    /// Account identifier for logging
    private final String name;

    /// Shared mutable state that needs protection
    private int balance;

    /// Constructor - sets initial state
    public BankAccount(String name, int balance) {
        this.name = name;
        this.balance = balance;
    }

    /// Returns account name for logging
    public String getName() {
        return name;
    }

    /// Adds amount to balance (synchronized for thread safety)
    public synchronized void deposit(int amount) {
        balance += amount;
    }

    /// Removes amount from balance (synchronized for thread safety)
    public synchronized void withdraw(int amount) {
        balance -= amount;
    }

    /// Returns current balance
    public int getBalance() {
        return balance;
    }
}

/// Task that transfers money between two accounts.
/// This implementation CAN cause deadlock due to inconsistent lock ordering.
class DeadlockTransferTask implements Runnable {
    private final BankAccount from;
    private final BankAccount to;
    private final int amount;

    public DeadlockTransferTask(BankAccount from, BankAccount to, int amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    @Override
    public void run() {
        /// Step 1: Acquire lock on 'from' account
        synchronized (from) {
            System.out.println(Thread.currentThread().getName() + " locked " + from.getName());

            /// Artificial delay to increase chance of deadlock
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }

            /// Step 2: Acquire lock on 'to' account (DEADLOCK RISK!)
            /// If another thread holds 'to' and wants 'from', deadlock occurs
            synchronized (to) {
                System.out.println(Thread.currentThread().getName() + " locked " + to.getName());
                from.withdraw(amount);
                to.deposit(amount);
                System.out.println("Transferred " + amount + " from " + from.getName() + " to " + to.getName());
            }
        }
    }
}

/// =================== 2. Prevention - Lock Ordering ===================

/// TECHNIQUE 1: Lock Ordering (Breaks Circular Wait Condition)
///
/// CONCEPT:
/// - Always acquire locks in a consistent global order
/// - Use a unique identifier (e.g., hashCode, account ID) to determine order
/// - This prevents circular dependencies
///
/// PROS:
/// ✓ Simple to implement
/// ✓ No timeout or retry logic needed
/// ✓ Deterministic behavior
/// ✓ Works with synchronized blocks
///
/// CONS:
/// ✗ Requires global ordering strategy
/// ✗ May reduce concurrency in some cases
/// ✗ All code must follow the same ordering convention

// Task that transfers money using consistent lock ordering.
// This prevents deadlock by always locking accounts in the same order.
class SafeTransferTask implements Runnable {
    private final BankAccount from;
    private final BankAccount to;
    private final int amount;

    public SafeTransferTask(BankAccount from, BankAccount to, int amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    @Override
    public void run() {
        /// Determine lock order using hashCode (could also use account ID)
        BankAccount first = from.hashCode() < to.hashCode() ? from : to;
        BankAccount second = from.hashCode() < to.hashCode() ? to : from;

        /// Always lock in the same order regardless of transfer direction
        synchronized (first) {
            System.out.println(Thread.currentThread().getName() + " locked " + first.getName());

            synchronized (second) {
                System.out.println(Thread.currentThread().getName() + " locked " + second.getName());
                from.withdraw(amount);
                to.deposit(amount);
                System.out.println("Transferred " + amount + " from " + from.getName() + " to " + to.getName());
            }
        }
    }
}


/// =================== 3. Prevention - tryLock with Timeout ===================

/// TECHNIQUE 2: tryLock with Timeout (Breaks Hold and Wait Condition)
///
/// CONCEPT:
/// - Use ReentrantLock.tryLock() instead of synchronized
/// - If lock cannot be acquired within timeout, release all locks and retry
/// - Prevents indefinite waiting
///
/// PROS:
/// ✓ Prevents indefinite blocking
/// ✓ Can recover from potential deadlock situations
/// ✓ Flexible timeout configuration
/// ✓ Works when lock ordering is not feasible
///
/// CONS:
/// ✗ More complex code (manual lock management)
/// ✗ May cause livelock if retry logic is not properly designed
/// ✗ Performance overhead from retries
/// ✗ Requires careful finally block management

// Bank account using explicit ReentrantLock instead of synchronized.
class BankAccountWithLock {
    private final String name;
    private int balance;
    private final Lock lock = new ReentrantLock();

    public BankAccountWithLock(String name, int balance) {
        this.name = name;
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public Lock getLock() {
        return lock;
    }

    /// Deposits amount (must be called while holding lock)
    public void deposit(int amount) {
        balance += amount;
    }

    /// Withdraws amount (must be called while holding lock)
    public void withdraw(int amount) {
        balance -= amount;
    }

    public int getBalance() {
        return balance;
    }
}

/// Transfer task using tryLock with timeout to avoid deadlock.
class TryLockTransferTask implements Runnable {
    private final BankAccountWithLock from;
    private final BankAccountWithLock to;
    private final int amount;

    public TryLockTransferTask(BankAccountWithLock from, BankAccountWithLock to, int amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    @Override
    public void run() {
        while (true) {
            /// Try to acquire both locks with timeout
            boolean gotFromLock = false;
            boolean gotToLock = false;

            try {
                /// Try to acquire 'from' lock (wait max 1 second)
                gotFromLock = from.getLock().tryLock(1, TimeUnit.SECONDS);
                if (!gotFromLock) {
                    System.out.println(Thread.currentThread().getName() + " couldn't lock " + from.getName() + ", retrying...");
                    continue; /// Retry
                }

                System.out.println(Thread.currentThread().getName() + " locked " + from.getName());

                /// Try to acquire 'to' lock (wait max 1 second)
                gotToLock = to.getLock().tryLock(1, TimeUnit.SECONDS);
                if (!gotToLock) {
                    System.out.println(Thread.currentThread().getName() + " couldn't lock " + to.getName() + ", retrying...");
                    continue; /// Will release 'from' lock in finally and retry
                }

                System.out.println(Thread.currentThread().getName() + " locked " + to.getName());

                /// Both locks acquired - perform transfer
                from.withdraw(amount);
                to.deposit(amount);
                System.out.println("Transferred " + amount + " from " + from.getName() + " to " + to.getName());
                break; /// Success - exit loop

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } finally {
                /// Release locks in reverse order
                if (gotToLock) {
                    to.getLock().unlock();
                }
                if (gotFromLock) {
                    from.getLock().unlock();
                }
            }
        }
    }
}


/// =================== Comparison Summary ===================

/// Prevention Technique Comparison:
///
/// Technique             | Breaks Condition     | Complexity | Best For
/// ----------------------|----------------------|------------|----------------------------------
/// Lock Ordering         | Circular Wait        | Low        | Known set of resources, fixed ordering possible
/// tryLock with Timeout  | Hold and Wait        | Medium     | Dynamic resources, unpredictable lock order
/// Single Lock           | Circular Wait        | Low        | Simple scenarios, acceptable contention
/// Lock-Free Algorithms  | Mutual Exclusion     | High       | High-performance, advanced use cases
///
/// General Deadlock Prevention Strategies:
/// 1. Avoid nested locks when possible
/// 2. Always acquire locks in a consistent order
/// 3. Use timeout-based locking (tryLock)
/// 4. Use higher-level concurrency utilities (e.g., Semaphore, CountDownLatch)
/// 5. Keep critical sections short
/// 6. Consider lock-free data structures (ConcurrentHashMap, AtomicInteger)

// =================== Main Entry Point ===================

// Demonstrates deadlock and various prevention techniques.
public class DeadlockAndPreventionTechniques {

    public static void main(String[] args) throws InterruptedException {

        /// ---------- Demo 1: Deadlock Scenario ----------
        System.out.println("=== Demo 1: Deadlock Scenario (Will Hang!) ===\n");
        /// Uncomment to see deadlock (program will hang)
        /// demoDeadlock();

        /// ---------- Demo 2: Lock Ordering Prevention ----------
        System.out.println("\n=== Demo 2: Lock Ordering Prevention ===\n");
        demoLockOrdering();

        /// ---------- Demo 3: tryLock with Timeout Prevention ----------
        System.out.println("\n=== Demo 3: tryLock with Timeout ===\n");
        demoTryLock();

        System.out.println("\nAll demos completed successfully!");
    }

    /// Demo 1: Shows deadlock scenario (DO NOT RUN - will hang forever)
    private static void demoDeadlock() throws InterruptedException {
        BankAccount accountA = new BankAccount("Account-A", 1000);
        BankAccount accountB = new BankAccount("Account-B", 1000);

        /// T1 transfers A → B
        Thread t1 = new Thread(new DeadlockTransferTask(accountA, accountB, 100), "T1");
        /// T2 transfers B → A (reverse order - causes deadlock!)
        Thread t2 = new Thread(new DeadlockTransferTask(accountB, accountA, 200), "T2");

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        /// This line is NEVER reached due to deadlock
        System.out.println("Both threads finished execution.");
    }

    /// Demo 2: Shows lock ordering preventing deadlock
    private static void demoLockOrdering() throws InterruptedException {
        BankAccount accountA = new BankAccount("Account-A", 1000);
        BankAccount accountB = new BankAccount("Account-B", 1000);

        /// Both threads use consistent lock ordering
        Thread t1 = new Thread(new SafeTransferTask(accountA, accountB, 100), "T1");
        Thread t2 = new Thread(new SafeTransferTask(accountB, accountA, 200), "T2");

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Both threads finished successfully!");
        System.out.println("Account-A balance: " + accountA.getBalance());
        System.out.println("Account-B balance: " + accountB.getBalance());
    }

    /// Demo 3: Shows tryLock with timeout preventing deadlock
    private static void demoTryLock() throws InterruptedException {
        BankAccountWithLock accountA = new BankAccountWithLock("Account-A", 1000);
        BankAccountWithLock accountB = new BankAccountWithLock("Account-B", 1000);

        /// Both threads use tryLock with timeout
        Thread t1 = new Thread(new TryLockTransferTask(accountA, accountB, 100), "T1");
        Thread t2 = new Thread(new TryLockTransferTask(accountB, accountA, 200), "T2");

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Both threads finished successfully!");
        System.out.println("Account-A balance: " + accountA.getBalance());
        System.out.println("Account-B balance: " + accountB.getBalance());
    }
}

