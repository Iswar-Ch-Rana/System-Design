package lld.Creational_Design_Patterns;

/// ## Singleton Pattern
/// A Single Object should exist for the class.
///
/// **Examples:** DB connection pool, Logger, Cache, Thread Pool, Configuration settings.

/// ### 1. Eager Loading
///
/// #### Understanding
/// - The object is created immediately when the class is loaded.
/// - It is always available and inherently thread-safe.
///
/// #### Pros
/// - Very simple to implement.
/// - Thread-safe without any extra handling.
///
/// #### Cons
/// - Wastes memory if the instance is never used.
/// - Not suitable for heavy objects.
class EagerSingleton {
    private static final EagerSingleton instance = new EagerSingleton();

    /// Private constructor to prevent instantiation using the `new` keyword.
    private EagerSingleton() {
    }

    /// Method to get the instance of the class.
    /// @return Always returns the same instance.
    public static EagerSingleton getInstance() {
        return instance; 
    }
}

/// ### 2. Lazy Loading
///
/// #### Understanding
/// - The object is created only when it is needed for the first time.
/// - It is not thread-safe by default, so additional synchronization is needed in multi-threaded environments.
///
/// #### Pros
/// - Saves memory if the instance is never used.
/// - Object creation is deferred until required.
///
/// #### Cons
/// - Not thread-safe by default. Thus, it requires synchronization in multi-threaded environments.
class LazySingleton {
    private static LazySingleton instance;

    /// Private constructor to prevent instantiation using the `new` keyword.
    private LazySingleton() {
    }

    /// Method to get the instance of the class.
    /// @return A new object if not created, otherwise the existing one.
    public static LazySingleton getInstance() {
        if (instance == null) {
            instance = new LazySingleton();
        }
        return instance;
    }
}

/// ### 3. Synchronized Method (Thread-Safe Lazy Loading)
///
/// #### Understanding
/// - The object is created only when it is needed for the first time, and the method is synchronized to ensure thread safety.
/// - It is thread-safe but can lead to performance issues due to synchronization overhead.
///
/// #### Pros
/// - Saves memory if the instance is never used.
/// - Thread-safe without complex synchronization logic.
///
/// #### Cons
/// - Synchronization can lead to performance issues, especially if `getInstance()` is called frequently after the instance has been created.
class Singleton {
    private static Singleton instance;

    private Singleton() {
    }

    /// Synchronized method to ensure thread safety.
    public static synchronized Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}

/// ### 4. Double-Checked Locking
///
/// #### Understanding
/// - The object is created only when it is needed for the first time, and synchronization is used only during the first creation of the instance.
/// - It is thread-safe and more efficient than the synchronized method.
///
/// #### Pros
/// - Efficient: Synchronization only happens once, when the instance is created.
/// - Safe and fast in concurrent environments.
///
/// #### Cons
/// - Slightly more complex than the synchronized method.
/// - Requires Java 1.5 or above due to reliance on `volatile`.
class Singleton1 {
    /// Volatile object declaration ensures visibility across threads.
    private static volatile Singleton1 instance;

    private Singleton1() {}

    /// Thread-safe method using double-checked locking.
    public static Singleton1 getInstance() {
        if (instance == null) {
            synchronized (Singleton1.class) {
                if (instance == null) {
                    instance = new Singleton1();
                }
            }
        }
        return instance;
    }
}

/// ### 5. Bill Pugh Singleton (Best Practice for Lazy Loading)
///
/// #### Explanation
/// - The Singleton instance is not created until `getInstance()` is called.
/// - The static inner class (`SingletonHelper`) is not loaded until referenced, thanks to Java's class loading mechanism.
/// - It ensures thread safety, lazy loading, and high performance without synchronization overhead.
///
/// #### Pros
/// - Best of both worlds: Lazy + Thread-safe.
/// - No need for `synchronized` or `volatile`.
/// - Clean and efficient.
///
/// #### Cons
/// - It is slightly less intuitive for beginners due to the use of a nested static class.
class BillPughSingleton {
    private BillPughSingleton() {}

    /// Static inner class that holds the Singleton instance.
    private static class SingletonHelper {
        private static final BillPughSingleton INSTANCE = new BillPughSingleton();
    }

    /// @return The single instance of `BillPughSingleton`.
    public static BillPughSingleton getInstance() {
        return SingletonHelper.INSTANCE;
    }
}

/// ## Summary of Singleton Pattern
///
/// ### Pros
/// - **Cleaner Implementation:** Offers a straightforward way to manage a single instance.
/// - **Guarantees One Instance:** Ideal for shared resources.
/// - **Global Resource Management:** Allows centralized access to application-wide configurations.
/// - **Supports Lazy Loading:** Optimizes memory usage and startup performance.
///
/// ### Cons
/// - **Confusion with Factory:** Can be confused with Factory pattern if parameters are used.
/// - **Testing Difficulty:** Global state makes unit testing/mocking hard.
/// - **Tight Coupling:** Components depending on it become tightly coupled.
/// - **Complexity for Concurrency:** Requires care to avoid race conditions.
/// - **Violates SRP:** Often handles both instance control and core functionality.
public class SingletonPattern {
    public static void main(String[] args) {
        EagerSingleton singleton1 = EagerSingleton.getInstance();
        EagerSingleton singleton2 = EagerSingleton.getInstance();

        // Both references point to the same instance
        System.out.println(singleton1 == singleton2); // Output: true

        LazySingleton lazySingleton1 = LazySingleton.getInstance();
        LazySingleton lazySingleton2 = LazySingleton.getInstance();

        System.out.println(lazySingleton1 == lazySingleton2);
    }
}
