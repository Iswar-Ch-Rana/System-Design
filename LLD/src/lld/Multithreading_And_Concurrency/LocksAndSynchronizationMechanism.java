package lld.Multithreading_And_Concurrency;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/// =================== 1. ReentrantLock - Basic Usage ===================

/// What is ReentrantLock?
/// - A mutual-exclusion lock with ownership semantics
/// - Only the thread that acquires it can release it
/// - The same thread can acquire the lock multiple times without deadlocking (reentrant)
///
/// Key Methods:
/// - lock.lock()   : Blocks until the lock is acquired
/// - lock.unlock() : Releases the lock (must be called in finally block)
/// - lock.tryLock(): Attempts to acquire lock without blocking
///
/// PROS:
/// ✓ Explicit control over when to lock and unlock (unlike synchronized)
/// ✓ Can attempt lock acquisition with timeout (tryLock with timeout)
/// ✓ Prevents indefinite blocking with tryLock()
/// ✓ Fine-grained control reduces contention
/// ✓ Supports fairness policy (optional)
/// ✓ Can interrupt thread waiting for lock
///
/// CONS:
/// ✗ More verbose than synchronized keyword
/// ✗ Must manually unlock in finally block (risk of deadlock if forgotten)
/// ✗ No automatic release like synchronized blocks
/// ✗ Slightly more complex to use correctly

/// Example: Ticket booking system using ReentrantLock.
/// Ensures only one thread can book a ticket at a time.
class TicketBooking {
    /// Shared resource: number of available seats
    private int availableSeats = 1;

    /// Lock to protect the shared resource
    private final ReentrantLock lock = new ReentrantLock();

    /// Books a ticket for the given user.
    /// Uses ReentrantLock to ensure thread-safe access.
    public void bookTicket(String user) {
        System.out.println(user + " is trying to book...");

        /// Step 1: Acquire the lock (blocks until available)
        lock.lock();
        try {
            System.out.println(user + " acquired lock.");

            /// Step 2: Critical section - check and update shared state
            if (availableSeats > 0) {
                System.out.println(user + " successfully booked the ticket.");
                availableSeats--;
            } else {
                System.out.println(user + " could not book the ticket. No seats left.");
            }
        } finally {
            /// Step 3: Always release the lock in finally block to avoid deadlocks
            System.out.println(user + " is releasing the lock.");
            lock.unlock();
        }
    }
}

/// =================== 2. ReentrantLock with Expiry Timer ===================

/// Advanced ReentrantLock example with auto-expiry.
/// Demonstrates how to build a lock that signals timeout after N milliseconds.
///
/// Important Note:
/// - Only the owner thread can unlock a ReentrantLock
/// - The scheduler thread cannot call unlock() directly
/// - Instead, we use a volatile flag to signal timeout
class ExpiringReentrantLock {
    /// The underlying lock for mutual exclusion
    private final ReentrantLock lock = new ReentrantLock();

    /// Scheduler to run the timeout task
    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    /// Flag to indicate if lock is held and should be monitored
    private volatile boolean isLocked = false;

    /// Attempts to acquire lock immediately.
    /// If successful, schedules a timeout signal after the specified duration.
    public boolean tryLockWithExpiry(long timeoutMillis) {
        /// Try to acquire lock without blocking
        boolean acquired = lock.tryLock();

        if (acquired) {
            /// Mark as locked
            isLocked = true;

            /// Schedule timeout signal
            /// Note: Scheduler cannot unlock directly (ownership restriction)
            /// It only clears the flag to signal the owner thread
            scheduler.schedule(() -> {
                if (isLocked) {
                    System.out.println("Timeout reached – signalling owner to release.");
                    isLocked = false;
                }
            }, timeoutMillis, TimeUnit.MILLISECONDS);
        }
        return acquired;
    }

    /// Releases the lock safely.
    /// Can be called by owner thread after timeout or during normal operation.
    public void unlockSafely() {
        if (lock.isHeldByCurrentThread()) {
            isLocked = false;
            lock.unlock();
            System.out.println("Lock released by " + Thread.currentThread().getName());
        }
    }

    /// Shuts down the scheduler service.
    public void shutdown() {
        scheduler.shutdownNow();
    }
}

/// =================== 3. ReentrantLock with tryLock (Timeout) ===================

/// Ticket booking system using tryLock with timeout.
/// Prevents indefinite blocking - gives up after specified time.
///
/// Use Case:
/// - User-facing applications where waiting too long is bad UX
/// - Systems that need to fail fast instead of blocking forever
class TicketBookingTryLock {
    /// Shared resource: available seats
    private int availableSeats = 1;

    /// Lock protecting seat updates
    private final ReentrantLock lock = new ReentrantLock();

    /// Attempts to book a ticket with a 2-second timeout.
    /// If lock cannot be acquired within 2 seconds, gives up.
    public void bookTicket(String user) {
        System.out.println(user + " is trying to book...");

        /// Track whether we acquired the lock
        boolean lockAcquired = false;

        try {
            /// Try to acquire lock, wait max 2 seconds
            lockAcquired = lock.tryLock(2, TimeUnit.SECONDS);

            if (lockAcquired) {
                System.out.println(user + " acquired lock.");

                /// Critical section: check and update seats
                if (availableSeats > 0) {
                    System.out.println(user + " successfully booked the ticket.");
                    availableSeats--;
                } else {
                    System.out.println(user + " could not book the ticket. No seats left.");
                }

                /// Simulate long operation to demonstrate timeout
                Thread.sleep(3000);
            } else {
                /// Failed to acquire lock within timeout
                System.out.println(user + " could not acquire lock. Try again later.");
            }
        } catch (InterruptedException e) {
            /// Restore interrupt status
            Thread.currentThread().interrupt();
            e.printStackTrace();
        } finally {
            /// Only unlock if we actually acquired it
            if (lockAcquired) {
                System.out.println(user + " is releasing the lock.");
                lock.unlock();
            }
        }
    }
}

/// =================== 4. ReadWriteLock ===================

/// What is ReadWriteLock?
/// - Allows multiple threads to read simultaneously
/// - Only one thread can write at a time
/// - Writers have exclusive access (no readers or other writers)
///
/// PROS:
/// ✓ Multiple readers can access data concurrently (high read throughput)
/// ✓ Better performance for read-heavy workloads
/// ✓ Readers don't block each other
/// ✓ Still ensures thread-safety for writes
///
/// CONS:
/// ✗ More complex than simple locks
/// ✗ Can cause writer starvation if reads are constant
/// ✗ Overhead is higher than simple locks
/// ✗ Not ideal for write-heavy workloads
///
/// Use Cases:
/// - Caching systems (many reads, few writes)
/// - Configuration data (read often, update rarely)
/// - Stock prices, game leaderboards
class StockData {
    /// Shared data: stock price
    private double price = 100.0;

    /// ReadWriteLock for concurrent reads
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /// Updates the stock price.
    /// Acquires write lock - exclusive access (no other readers or writers).
    public void updatePrice(double newPrice) {
        lock.writeLock().lock();
        try {
            System.out.printf("%s updating price to %.2f%n",
                    Thread.currentThread().getName(), newPrice);
            price = newPrice;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /// Reads the stock price.
    /// Acquires read lock - shared access (multiple readers allowed).
    public void readPrice() {
        lock.readLock().lock();
        try {
            System.out.printf("%s read price: %.2f%n",
                    Thread.currentThread().getName(), price);
        } finally {
            lock.readLock().unlock();
        }
    }
}

/// =================== 5. Semaphore ===================

/// What is Semaphore?
/// - Controls access to a resource with a limited number of permits
/// - Allows N threads to access the resource concurrently
/// - Unlike locks, has no ownership (any thread can release a permit)
///
/// PROS:
/// ✓ Limits concurrent access to N threads (not just 1)
/// ✓ Perfect for connection pools, rate limiting
/// ✓ Can implement counting-based resource management
/// ✓ tryAcquire() prevents indefinite blocking
/// ✓ Simple API for permit-based access control
///
/// CONS:
/// ✗ No ownership semantics (any thread can release)
/// ✗ Not reentrant (same thread can't acquire multiple times without blocking)
/// ✗ No built-in wait/notify mechanism
/// ✗ Must manually track acquire/release pairs
/// ✗ Can lead to permit leaks if release is forgotten
///
/// Use Cases:
/// - Database connection pools (limit max connections)
/// - Rate limiting (max N requests per second)
/// - Resource pools (thread pools, object pools)
/// - Multi-device login limits (like Netflix, YouTube Premium)
class TUFPlusAccount {
    /// Semaphore with N permits (N = max devices allowed)
    private final Semaphore deviceSlots;

    /// Creates account with max device limit.
    public TUFPlusAccount(int maxDevices) {
        this.deviceSlots = new Semaphore(maxDevices);
    }

    /// Attempts to log in a user.
    /// Returns true if a device slot is available, false otherwise.
    public boolean login(String user) throws InterruptedException {
        System.out.println(user + " trying to log in...");

        /// Try to acquire a permit (non-blocking)
        if (deviceSlots.tryAcquire()) {
            System.out.println(user + " successfully logged in.");
            return true;
        } else {
            System.out.println(user + " denied login - too many devices.");
            return false;
        }
    }

    /// Logs out a user and releases the device slot.
    public void logout(String user) {
        System.out.println(user + " logging out.");
        deviceSlots.release();  /// Return the permit
    }
}

/// Demo: Semaphore in action with 2-device limit.
/// Third user should be denied when first two are logged in.
class SemaphoreDemo {
    public static void main(String[] args) throws InterruptedException {
        TUFPlusAccount account = new TUFPlusAccount(2);

        Thread u1 = new Thread(() -> trySession(account, "User-A"));
        Thread u2 = new Thread(() -> trySession(account, "User-B"));
        Thread u3 = new Thread(() -> trySession(account, "User-C")); /// Should be denied

        u1.start();
        u2.start();
        Thread.sleep(100);  /// Ensure first two log in
        u3.start();

        u1.join();
        u2.join();
        u3.join();
    }

    /// Helper method to simulate a user session.
    private static void trySession(TUFPlusAccount acc, String name) {
        try {
            if (acc.login(name)) {
                Thread.sleep(500);  /// Simulate watching a video
                acc.logout(name);
            }
        } catch (InterruptedException ignored) {
        }
    }
}

/// =================== Comparison Summary ===================

/// Comparison of synchronization mechanisms:
///
/// Feature                  | synchronized          | ReentrantLock                | Semaphore
/// -------------------------|-----------------------|------------------------------|---------------------------
/// Type                     | Intrinsic (built-in)  | Explicit Lock API            | Permit-based access
/// Concurrency Limit        | 1 thread              | 1 thread                     | N threads
/// Reentrant                | ✅ Yes                | ✅ Yes                       | ❌ No
/// Timeout Support          | ❌ No                 | ✅ Yes (tryLock)             | ✅ Yes (tryAcquire)
/// Try Lock (non-blocking)  | ❌ No                 | ✅ Yes                       | ✅ Yes
/// Interruptible            | ❌ No                 | ✅ Yes                       | ✅ Yes
/// Fairness Policy          | ❌ No                 | ✅ Yes (optional)            | ✅ Yes (optional)
/// Wait/Notify              | ✅ Yes (wait/notify)  | ✅ Yes (Condition.await())   | ❌ No
/// Ownership Semantics      | Thread-bound          | Thread-bound                 | No ownership (permits)
/// Complexity               | Simple                | Moderate                     | Simple
/// Performance              | Good                  | Very Good (low contention)   | Good
///
/// Best Use Cases:
/// - synchronized:    Simple critical sections, producer-consumer
/// - ReentrantLock:   Fine-grained control, tryLock scenarios, ticket booking
/// - Semaphore:       Resource pools (DB connections), rate limiting, multi-device login

/// =================== Main Entry Point ===================

/// Demonstrates various locking and synchronization mechanisms in Java.
/// Run different demos to see how each mechanism works.
public class LocksAndSynchronizationMechanism {

    public static void main(String[] args) {

        /// ---------- Demo 1: Basic ReentrantLock ----------
        System.out.println("=== Demo 1: Basic ReentrantLock ===\n");
        demoBasicReentrantLock();

        /// ---------- Demo 2: ReentrantLock with Expiry ----------
        System.out.println("\n=== Demo 2: ReentrantLock with Expiry ===\n");
        demoExpiringLock();

        /// ---------- Demo 3: tryLock with Timeout ----------
        System.out.println("\n=== Demo 3: tryLock with Timeout ===\n");
        demoTryLock();

        /// ---------- Demo 4: ReadWriteLock ----------
        System.out.println("\n=== Demo 4: ReadWriteLock ===\n");
        demoReadWriteLock();

        /// ---------- Demo 5: Semaphore ----------
        System.out.println("\n=== Demo 5: Semaphore ===\n");
        try {
            SemaphoreDemo.main(args);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /// Demo 1: Basic ReentrantLock usage.
    /// Two users try to book the same ticket simultaneously.
    private static void demoBasicReentrantLock() {
        TicketBooking bookingSystem = new TicketBooking();

        Thread user1 = new Thread(() -> bookingSystem.bookTicket("User 1"));
        Thread user2 = new Thread(() -> bookingSystem.bookTicket("User 2"));

        user1.start();
        user2.start();

        try {
            user1.join();
            user2.join();
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted: " + e.getMessage());
        }
    }

    /// Demo 2: Expiring ReentrantLock.
    /// Idle user holds lock, active user waits and retries.
    private static void demoExpiringLock() {
        ExpiringReentrantLock expLock = new ExpiringReentrantLock();

        /// Idle user: acquires lock then goes idle for 5 seconds
        Thread idleUser = new Thread(() -> {
            if (expLock.tryLockWithExpiry(3000)) {
                System.out.println("IdleUser acquired lock, going idle...");
                try {
                    Thread.sleep(5000);  /// Simulate idle time
                } catch (InterruptedException ignored) {
                }
                expLock.unlockSafely();
            }
        }, "IdleUser");

        /// Active user: waits and retries until lock is available
        Thread activeUser = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }

            while (true) {
                if (expLock.tryLockWithExpiry(3000)) {
                    System.out.println("ActiveUser acquired lock!");
                    expLock.unlockSafely();
                    break;
                } else {
                    System.out.println("ActiveUser still waiting...");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        }, "ActiveUser");

        idleUser.start();
        activeUser.start();

        try {
            idleUser.join();
            activeUser.join();
        } catch (InterruptedException ignored) {
        }

        expLock.shutdown();
    }

    /// Demo 3: tryLock with timeout.
    /// Second user times out waiting for the lock.
    private static void demoTryLock() {
        TicketBookingTryLock booking = new TicketBookingTryLock();

        /// User 1: books immediately
        Thread user1 = new Thread(() -> booking.bookTicket("User 1"));

        /// User 2: arrives later and may timeout
        Thread user2 = new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
            booking.bookTicket("User 2");
        });

        user1.start();
        user2.start();

        try {
            user1.join();
            user2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /// Demo 4: ReadWriteLock with multiple readers.
    /// Multiple threads can read concurrently, but writes are exclusive.
    private static void demoReadWriteLock() {
        StockData stockData = new StockData();

        /// Create 3 reader threads
        Thread reader1 = new Thread(() -> stockData.readPrice(), "Reader-1");
        Thread reader2 = new Thread(() -> stockData.readPrice(), "Reader-2");
        Thread reader3 = new Thread(() -> stockData.readPrice(), "Reader-3");

        /// Create 1 writer thread
        Thread writer = new Thread(() -> stockData.updatePrice(105.50), "Writer-1");

        /// Start all threads
        reader1.start();
        reader2.start();
        writer.start();
        reader3.start();

        try {
            reader1.join();
            reader2.join();
            reader3.join();
            writer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
