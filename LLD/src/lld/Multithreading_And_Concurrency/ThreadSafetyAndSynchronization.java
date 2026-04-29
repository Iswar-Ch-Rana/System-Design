package lld.Multithreading_And_Concurrency;

import java.util.concurrent.atomic.AtomicInteger;

/// Demonstrates thread safety using the `synchronized` keyword.
///
/// Key idea:
/// - The shared variable is protected by the intrinsic lock of the object.
/// - Only one thread can execute the synchronized method at a time.
/// - This gives both atomicity and visibility.
class PurchaseCounter {
    /// Shared mutable state.
    private int count = 0;

    /// Increments the counter in a thread-safe way.
    ///
    /// The statement `count++` is not atomic by itself because it
    /// involves read, update, and write steps. Synchronization makes the whole
    /// sequence execute safely as one critical section.
    synchronized public void increment() {
        count++;
    }

    /// Returns the current counter value.
    public int getCount() {
        return count;
    }
}

/// Demonstrates the use of `volatile`.
///
/// Key idea:
/// - `volatile` guarantees visibility of writes across threads.
/// - It does not make compound operations atomic.
/// - It is useful for status flags and simple communication between threads.
class SharedData {
    /// Visibility is guaranteed for this flag.
    volatile boolean flag = false;

    /// Writes to the volatile flag.
    ///
    /// Once this method sets the flag to `true`, other threads reading
    /// the flag will observe the updated value.
    public void writer() {
        flag = true;
    }

    /// Reads the volatile flag.
    ///
    /// If another thread has already written `true`, this thread is
    /// guaranteed to see that latest value.
    public void reader() {
        if (flag) {
            // Visible across threads because the field is volatile.
        }
    }
}


/// Demonstrates thread safety using `AtomicInteger`.
///
/// Key idea:
/// - `AtomicInteger` provides lock-free atomic updates.
/// - It internally uses CAS (compare-and-set).
/// - It is a good fit for counters and lightweight shared state updates.
class PurchaseAtomicCounter {

    /// Atomic counter backed by CAS operations.
    private final AtomicInteger likes = new AtomicInteger(0);

    /// Increments the counter using an explicit CAS retry loop.
    ///
    /// This method is intentionally written in expanded form to show how
    /// atomic update logic works internally.
    public void incrementLikes() {
        int prev, next;
        do {
            // Step 1: read current value
            prev = likes.get();

            // Step 2: compute new value
            next = prev + 1;

            // Step 3: update only if no other thread changed it first
        } while (!likes.compareAndSet(prev, next));
    }

    /// Returns the current atomic counter value.
    public int getCount() {
        return likes.get();
    }
}


/// Runs a simple multi-threading demo.
///
/// Two threads increment:
/// - a synchronized counter
/// - an atomic counter
///
/// Both counters should produce the correct final result because both
/// implementations are thread-safe.
class RaceConditionDemo {
    /// Entry point for the concurrency demo.
    public static void main(String[] args) throws InterruptedException {
        PurchaseCounter counter = new PurchaseCounter();
        PurchaseAtomicCounter counter1 = new PurchaseAtomicCounter();

        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                counter.increment();
                counter1.incrementLikes();
            }
        };

        // Run the same task in two threads
        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Final Count: " + counter.getCount());
        System.out.println("Final Atomic Count: " + counter1.getCount());
    }
}

/// Revision notes for thread safety and synchronization.
///
/// Comparison summary:
/// - `synchronized`: provides atomicity and visibility, but uses blocking.
/// - `volatile`: provides visibility only, so it is not enough for compound updates.
/// - `AtomicInteger`: provides atomicity and visibility without explicit locking for simple numeric updates.
///
/// Best use cases:
/// - Use `synchronized` for critical sections involving multiple steps.
/// - Use `volatile` for state flags or one-way thread communication.
/// - Use `AtomicInteger` for counters and lightweight concurrent updates.
public class ThreadSafetyAndSynchronization {
    /// Placeholder main method for this notes class.
    public static void main(String[] args) {

    }
}
