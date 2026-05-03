package lld.ExceptionAndErrorHandling;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/// ═══════════════════════════════════════════════════════════════════════════════
/// EXCEPTION & ERROR HANDLING IN JAVA
/// ═══════════════════════════════════════════════════════════════════════════════
///
/// Exception Hierarchy:
///   Throwable
///   ├── Error (JVM-level, unrecoverable: OutOfMemoryError, StackOverflowError)
///   └── Exception
///       ├── Checked Exceptions (compile-time: IOException, SQLException)
///       └── RuntimeException / Unchecked (NullPointerException, IllegalArgumentException)
///
/// ─────────────────────────────────────────────────────────────────────────────
/// Checked vs Unchecked Exceptions:
/// ─────────────────────────────────────────────────────────────────────────────
/// Feature              | Checked Exception            | Unchecked Exception (RuntimeException)
/// ─────────────────────|─────────────────────────────-|────────────────────────────────────────
/// Compile-time check   | ✅ Yes                       | ❌ No
/// Must handle/declare  | ✅ Yes (try-catch or throws) | ❌ No (optional)
/// Recoverable?         | Usually yes                  | Usually programming bugs
/// Examples             | IOException, SQLException    | NullPointerException, IllegalArgumentException
/// When to use          | External failures (IO, DB)   | Logic errors, invalid args, broken contracts
///
/// ─────────────────────────────────────────────────────────────────────────────
/// Fail-Fast vs Fail-Safe Strategy:
/// ─────────────────────────────────────────────────────────────────────────────
/// Feature          | Fail-Fast                        | Fail-Safe
/// ─────────────────|──────────────────────────────────|─────────────────────────────────────
/// Philosophy       | Crash immediately on error       | Recover gracefully, continue running
/// When to use      | Critical operations, data integrity | Non-critical, user-facing, fallbacks
/// Pros             | Bugs found early, clear stack trace | System stays up, better UX
/// Cons             | Harsh for users, service may stop  | May hide bugs, stale/default data
/// Example          | Validate input → throw exception | Catch exception → return default
/// ═══════════════════════════════════════════════════════════════════════════════
///
///
/// ─────────────────────────── FAIL-FAST EXAMPLE ───────────────────────────────
/// When to Use:
///   - Input validation at service boundaries
///   - When invalid state must NOT propagate further
///   - Internal APIs where callers should fix their code
///
/// Pros:
///   - Detects bugs early in the call chain
///   - Clear, informative error messages
///   - Prevents corrupted state downstream
///
/// Cons:
///   - Can feel harsh for end-users if not caught upstream
///   - Requires callers to handle exceptions properly

class ProductServiceFailFirst {
    public void getProduct(String productId) {
        // Fail-fast: reject invalid input immediately
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (productId.isBlank()) {
            throw new IllegalArgumentException("Product ID cannot be blank");
        }
        // proceed only with valid input
        // return productRepo.find(productId);
    }
}


// ─────────────────────────── FAIL-SAFE EXAMPLE ───────────────────────────────

/// When to Use:
///   - Searching across multiple data sources (any one may fail)
///   - User-facing features where partial data is better than no data
///   - Non-critical operations (logging, analytics, recommendations)
///
/// Pros:
///   - System remains operational despite partial failures
///   - Better user experience (graceful degradation)
///   - Works well with circuit-breaker patterns
///
/// Cons:
///   - May mask real bugs (silent failures)
///   - Stale or default data might confuse downstream logic
///   - Harder to debug if no logging is done

class ProductServiceFailSafe {
    public Object getProduct(String productId) {
        try {
            // return productRepo.find(productId);
            return null; // placeholder for actual repo call
        } catch (Exception e) {
            // Fail-safe: log the error and return a sensible default
            System.out.println("Failed to fetch product: " + e.getMessage());
            return "Fallback Product"; // return default instead of crashing
        }
    }
}


// ─────────────────────────── CHECKED EXCEPTION EXAMPLE ───────────────────────

/// IOException is a checked exception.
/// The compiler FORCES you to either:
///   1. Catch it with try-catch, OR
///   2. Declare it with 'throws' in method signature
///
/// When to Use Checked Exceptions:
///   - Recoverable situations (file not found → ask user for correct path)
///   - External system failures (network, database, file I/O)
///
/// Pros:
///   - Compiler ensures handling – nothing is missed
///   - Self-documenting: method signature tells callers what can go wrong
///
/// Cons:
///   - Verbose boilerplate (try-catch everywhere)
///   - Can clutter code if overused
///   - Leaks implementation details in method signatures

class FileReaderExample {

    // 'throws IOException' → caller MUST handle this
    public void readFile(String filePath) throws IOException {
        // try-with-resources: auto-closes reader even on exception
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
        }
        // No need for explicit close – try-with-resources handles it
    }

    public static void main(String[] args) {
        FileReaderExample example = new FileReaderExample();
        try {
            example.readFile("somefile.txt");
        } catch (FileNotFoundException e) {
            // Specific catch first – better error message
            System.out.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            // General IO error
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
}


// ─────────────────────────── CUSTOM EXCEPTION EXAMPLE ────────────────────────

/// When to Create Custom Exceptions:
///   - Domain-specific errors that standard exceptions don't express well
///   - When you want to carry extra context (userId, orderId, etc.)
///   - To distinguish business logic errors from system errors
///
/// Pros:
///   - Meaningful naming (CustomerNotPlusException tells you exactly what happened)
///   - Can carry domain-specific fields (userId, errorCode, etc.)
///   - Easy to catch selectively without inspecting message strings
///
/// Cons:
///   - Too many custom exceptions can clutter the codebase
///   - Needs documentation so other devs know when they're thrown
///
/// Rule of Thumb:
///   - Extend RuntimeException for programming/logic errors (unchecked)
///   - Extend Exception for recoverable situations caller must handle (checked)

class CustomerNotPlusException extends RuntimeException {
    private final String userId;

    public CustomerNotPlusException(String userId) {
        super("User " + userId + " is not a Plus customer");
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}

class CourseService {

    public void accessCourse(String userId) {
        // Fail-fast: validate access before proceeding
        if (!hasAccess(userId)) {
            throw new CustomerNotPlusException(userId);
        }
        // If we reach here, user has access → proceed with enrollment
        System.out.println("User " + userId + " enrolled in course.");
    }

    private boolean hasAccess(String userId) {
        // In real app: query DB to check subscription status
        return false; // simulating no access for demo
    }
}


// ─────────────────────────── BEST PRACTICES ──────────────────────────────────

/// 1. Use try-with-resources for AutoCloseable resources (streams, connections)
/// 2. Catch specific exceptions before general ones
/// 3. Never catch Exception/Throwable in production without re-throwing or logging
/// 4. Don't use exceptions for flow control (expensive: stack trace creation)
/// 5. Always log the exception or re-throw – never swallow silently
/// 6. Prefer unchecked exceptions for programming errors
/// 7. Prefer checked exceptions for recoverable external failures
/// 8. Add meaningful messages with context (IDs, parameters)
/// 9. Use @throws / @exception Javadoc to document thrown exceptions
/// 10. Consider exception chaining: throw new XException("msg", cause)


// ─────────────────────────── DEMO ────────────────────────────────────────────
public class ExceptionHandling {
    public static void main(String[] args) {

        // --- Fail-Fast Demo ---
        System.out.println("=== Fail-Fast Demo ===");
        ProductServiceFailFirst failFirst = new ProductServiceFailFirst();
        try {
            failFirst.getProduct(null); // will throw IllegalArgumentException
        } catch (IllegalArgumentException e) {
            System.out.println("Caught: " + e.getMessage());
        }

        // --- Fail-Safe Demo ---
        System.out.println("\n=== Fail-Safe Demo ===");
        ProductServiceFailSafe failSafe = new ProductServiceFailSafe();
        Object product = failSafe.getProduct("P123");
        System.out.println("Got: " + product);

        // --- Checked Exception Demo ---
        System.out.println("\n=== Checked Exception (File IO) Demo ===");
        FileReaderExample fileExample = new FileReaderExample();
        try {
            fileExample.readFile("nonexistent.txt");
        } catch (IOException e) {
            System.out.println("Handled checked exception: " + e.getMessage());
        }

        // --- Custom Exception Demo ---
        System.out.println("\n=== Custom Exception Demo ===");
        CourseService courseService = new CourseService();
        try {
            courseService.accessCourse("user_42");
        } catch (CustomerNotPlusException e) {
            System.out.println("Caught custom exception: " + e.getMessage());
            System.out.println("User ID: " + e.getUserId());
        }
    }
}
