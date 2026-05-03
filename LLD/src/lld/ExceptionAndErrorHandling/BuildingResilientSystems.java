package lld.ExceptionAndErrorHandling;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/// ╔════════════════════════════════════════════════════════════════════════════╗
/// ║              BUILDING RESILIENT SYSTEMS – STUDY NOTES                      ║
/// ╠════════════════════════════════════════════════════════════════════════════╣
/// ║                                                                            ║
/// ║  Resilience = the ability of a system to handle failures gracefully        ║
/// ║  without cascading into full outages.                                      ║
/// ║                                                                            ║
/// ║  Key Resilience Strategies:                                                ║
/// ║  ┌────────────────────────────────────────────────────────────────────┐    ║
/// ║  │ 1. Fallback        – Provide alternate response on failure         │    ║
/// ║  │ 2. Retry           – Re-attempt a failed operation                 │    ║
/// ║  │ 3. Backoff         – Increase delay between retries                │    ║
/// ║  │ 4. Circuit Breaker – Stop calling a failing service temporarily    │    ║
/// ║  │ 5. Timeout         – Don't wait forever for a response             │    ║
/// ║  │ 6. Bulkhead        – Isolate failures to prevent cascading         │    ║
/// ║  └────────────────────────────────────────────────────────────────────┘    ║
/// ║                                                                            ║
/// ║  Comparison Table:                                                         ║
/// ║  ┌──────────────────┬───────────────────────┬──────────────────────────┐   ║
/// ║  │ Strategy         │ When to Use           │ Risk if Misused          │   ║
/// ║  ├──────────────────┼───────────────────────┼──────────────────────────┤   ║
/// ║  │ Fallback         │ Non-critical data     │ Stale/incorrect data     │   ║
/// ║  │ Naive Retry      │ Transient failures    │ DDoS on downstream svc   │   ║
/// ║  │ Exponential Back  │ Rate-limited APIs    │ Increased latency        │   ║
/// ║  │ Circuit Breaker  │ Chronic failures      │ Requests rejected early  │   ║
/// ║  │ Timeout          │ All external calls    │ False failures if short  │   ║
/// ║  │ Bulkhead         │ Multi-tenant systems  │ Wasted resources         │   ║
/// ║  └──────────────────┴───────────────────────┴──────────────────────────┘   ║
/// ╚════════════════════════════════════════════════════════════════════════════╝


// ═══════════════════════════════════════════════════════════════════════════════
// 1. FALLBACK PATTERN
// ═══════════════════════════════════════════════════════════════════════════════

/// When to Use:
///   - The downstream service is non-critical (recommendations, ads, ETA)
///   - A degraded but usable response is acceptable
///   - You have cached/default data to serve
///
/// Pros:
///   ✅ System remains available even when dependencies fail
///   ✅ Users see a response instead of an error page
///   ✅ Easy to implement with try-catch + cache layer
///
/// Cons:
///   ❌ Fallback data may be stale or inaccurate
///   ❌ Can mask underlying bugs if overused
///   ❌ Need to monitor how often fallback is triggered

// Fallback Types:
//   1. Return cached data        → e.g., cached recommendations
//   2. Return default value      → e.g., "Menu unavailable"
//   3. Queue for later           → e.g., retry payment asynchronously

class RecommendationService {

    // Simulates a cache of pre-computed recommendations
    private final List<String> cachedRecommendations =
            Arrays.asList("cached-movie-1", "cached-movie-2", "cached-movie-3");

    // Simulates whether the live service is healthy
    private boolean liveServiceUp = false;

    public List<String> getRecommendedItems(String userId) {
        try {
            // Attempt to fetch live recommendations
            return fetchLiveRecommendations(userId);
        } catch (Exception ex) {
            // Fallback: serve from cache if live service fails
            System.out.println("[WARN] Live service failed for " + userId
                    + ", falling back to cache.");
            return cachedRecommendations;
        }
    }

    private List<String> fetchLiveRecommendations(String userId) {
        if (!liveServiceUp) {
            throw new RuntimeException("Live recommendation service is down!");
        }
        return List.of("live-movie-1", "live-movie-2");
    }

    public void setLiveServiceUp(boolean up) {
        this.liveServiceUp = up;
    }
}


// ═══════════════════════════════════════════════════════════════════════════════
// 2. RETRY PATTERN (Naive)
// ═══════════════════════════════════════════════════════════════════════════════

/// When to Use:
///   - Failures are transient (network blips, temporary unavailability)
///   - The operation is idempotent (safe to repeat)
///
/// Pros:
///   ✅ Simple to implement
///   ✅ Handles short-lived failures well
///   ✅ No complex state management needed
///
/// Cons:
///   ❌ Can overwhelm downstream services (thundering herd)
///   ❌ Tight retry loop wastes CPU
///   ❌ Not suitable for non-idempotent operations (e.g., payments)
///   ❌ Can cause DDoS on the failing service

class ETAServiceNaiveRetry {

    private final AtomicInteger callCount = new AtomicInteger(0);
    private final int failUntilAttempt; // will succeed on this attempt number

    public ETAServiceNaiveRetry(int failUntilAttempt) {
        this.failUntilAttempt = failUntilAttempt;
    }

    // Simulates fetching ETA — fails until the Nth call
    private String fetchETA() {
        int attempt = callCount.incrementAndGet();
        if (attempt < failUntilAttempt) {
            throw new RuntimeException("ETA service timeout (attempt " + attempt + ")");
        }
        return "ETA: 25 minutes";
    }

    // Naive retry: just loop N times
    public String getETAWithRetry(int maxRetries) {
        int retries = maxRetries;
        while (retries-- > 0) {
            try {
                return fetchETA();
            } catch (Exception e) {
                System.out.println("[RETRY] " + e.getMessage()
                        + " | attempts left: " + retries);
            }
        }
        return "ETA unavailable (all retries exhausted)";
    }
}


// ═══════════════════════════════════════════════════════════════════════════════
// 3. EXPONENTIAL BACKOFF RETRY
// ═══════════════════════════════════════════════════════════════════════════════

/// When to Use:
///   - Downstream service is rate-limited or overloaded
///   - You want to give the failing service time to recover
///   - Combined with jitter for distributed systems
///
/// Pros:
///   ✅ Reduces load on failing services
///   ✅ Prevents thundering herd problem
///   ✅ Industry standard (AWS, GCP SDKs use this)
///
/// Cons:
///   ❌ Increases overall latency for the caller
///   ❌ Must cap the max delay to avoid infinite waits
///   ❌ Without jitter, retries can still synchronize
///
/// Formula: delay = initialDelay × 2^(attemptNumber)
/// With Jitter: delay = random(0, initialDelay × 2^(attemptNumber))

class ETAServiceBackoffRetry {

    private final AtomicInteger callCount = new AtomicInteger(0);
    private final int failUntilAttempt;

    public ETAServiceBackoffRetry(int failUntilAttempt) {
        this.failUntilAttempt = failUntilAttempt;
    }

    private String fetchETA() {
        int attempt = callCount.incrementAndGet();
        if (attempt < failUntilAttempt) {
            throw new RuntimeException("ETA service timeout (attempt " + attempt + ")");
        }
        return "ETA: 25 minutes";
    }

    // Retry with exponential backoff
    public String getETAWithBackoff(int maxRetries, long initialDelayMs) {
        int retries = maxRetries;
        long delay = initialDelayMs;

        while (retries-- > 0) {
            try {
                return fetchETA();
            } catch (Exception e) {
                System.out.println("[BACKOFF] " + e.getMessage()
                        + " | waiting " + delay + "ms before next retry");
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return "ETA unavailable (interrupted)";
                }
                delay *= 2; // double the delay each time
            }
        }
        return "ETA unavailable (all retries exhausted)";
    }
}


// ═══════════════════════════════════════════════════════════════════════════════
// 4. CIRCUIT BREAKER PATTERN
// ═══════════════════════════════════════════════════════════════════════════════

/// When to Use:
///   - A dependency is chronically failing (not just transient)
///   - You want to fail fast instead of waiting for timeouts
///   - Prevent cascading failures across microservices
///
/// Pros:
///   ✅ Stops wasting resources on a known-broken service
///   ✅ Allows downstream service time to recover
///   ✅ Provides clear feedback: "service unavailable"
///   ✅ Self-healing: automatically probes to see if service recovered
///
/// Cons:
///   ❌ More complex to implement correctly
///   ❌ Needs tuning (threshold, window size, half-open count)
///   ❌ May reject valid requests during OPEN state
///
/// States:
///   ┌─────────┐    failures > threshold    ┌────────┐
///   │ CLOSED  │ ─────────────────────────▶ │  OPEN  │
///   │(normal) │                            │(reject)│
///   └─────────┘                            └────────┘
///       ▲                                       │
///       │    success in half-open          wait duration elapsed
///       │                                       │
///       │         ┌───────────┐                 │
///       └──────── │ HALF_OPEN │ ◀───────────────┘
///                 │ (probe)   │
///                 └───────────┘
///                     │ failure → back to OPEN

/// In Production (Spring Boot + Resilience4j):
///
/// @CircuitBreaker(name = "paymentService", fallbackMethod = "paymentFallback")
///   public String charge(String userId, double amount) { ... }
///
///   application.yml config:
///     resilience4j.circuitbreaker.instances.paymentService:
///       slidingWindowSize: 10
///       failureRateThreshold: 50
///       waitDurationInOpenState: 10s
///       permittedNumberOfCallsInHalfOpenState: 2

// Simple Circuit Breaker implementation for demonstration
class SimpleCircuitBreaker {

    enum State {CLOSED, OPEN, HALF_OPEN}

    private State state = State.CLOSED;
    private int failureCount = 0;
    private final int failureThreshold;
    private final long openStateDurationMs;
    private long lastFailureTime = 0;

    public SimpleCircuitBreaker(int failureThreshold, long openStateDurationMs) {
        this.failureThreshold = failureThreshold;
        this.openStateDurationMs = openStateDurationMs;
    }

    public State getState() {
        // Auto-transition from OPEN → HALF_OPEN after wait duration
        if (state == State.OPEN) {
            long elapsed = System.currentTimeMillis() - lastFailureTime;
            if (elapsed >= openStateDurationMs) {
                state = State.HALF_OPEN;
                System.out.println("[CB] Transitioning to HALF_OPEN (probing...)");
            }
        }
        return state;
    }

    // Check if request is allowed
    public boolean allowRequest() {
        State currentState = getState();
        return currentState == State.CLOSED || currentState == State.HALF_OPEN;
    }

    // Record a success — reset failure count
    public void recordSuccess() {
        failureCount = 0;
        if (state == State.HALF_OPEN) {
            state = State.CLOSED;
            System.out.println("[CB] Success in HALF_OPEN → back to CLOSED");
        }
    }

    // Record a failure — may trip the circuit
    public void recordFailure() {
        failureCount++;
        lastFailureTime = System.currentTimeMillis();
        if (state == State.HALF_OPEN) {
            state = State.OPEN;
            System.out.println("[CB] Failure in HALF_OPEN → back to OPEN");
        } else if (failureCount >= failureThreshold) {
            state = State.OPEN;
            System.out.println("[CB] Failure threshold reached → OPEN (rejecting calls)");
        }
    }

    @Override
    public String toString() {
        return "CircuitBreaker{state=" + state + ", failures=" + failureCount + "}";
    }
}

// Payment service protected by a circuit breaker
class PaymentService {

    private final SimpleCircuitBreaker circuitBreaker;
    private boolean serviceUp;

    public PaymentService(boolean serviceUp, int failureThreshold, long openDurationMs) {
        this.serviceUp = serviceUp;
        this.circuitBreaker = new SimpleCircuitBreaker(failureThreshold, openDurationMs);
    }

    public String charge(String userId, double amount) {
        // Check circuit breaker before making the call
        if (!circuitBreaker.allowRequest()) {
            System.out.println("[PaymentService] Circuit OPEN – fast-failing for " + userId);
            return paymentFallback(userId, amount);
        }

        try {
            // Simulate external payment call
            String result = callExternalPaymentApi(userId, amount);
            circuitBreaker.recordSuccess();
            return result;
        } catch (Exception e) {
            circuitBreaker.recordFailure();
            System.out.println("[PaymentService] Call failed: " + e.getMessage());
            return paymentFallback(userId, amount);
        }
    }

    private String callExternalPaymentApi(String userId, double amount) {
        if (!serviceUp) {
            throw new RuntimeException("Payment gateway timeout");
        }
        return "Payment of $" + amount + " charged to " + userId;
    }

    // Fallback when payment fails or circuit is open
    private String paymentFallback(String userId, double amount) {
        return "PAYMENT_QUEUED (will retry for " + userId + ", $" + amount + ")";
    }

    public void setServiceUp(boolean serviceUp) {
        this.serviceUp = serviceUp;
    }

    public SimpleCircuitBreaker getCircuitBreaker() {
        return circuitBreaker;
    }
}


// ═══════════════════════════════════════════════════════════════════════════════
// 5. TIMEOUT PATTERN
// ═══════════════════════════════════════════════════════════════════════════════

/// When to Use:
///   - Every external call (HTTP, DB, cache, message queue)
///   - You never want a thread blocked indefinitely
///
/// Pros:
///   ✅ Prevents threads from hanging forever
///   ✅ Frees resources quickly on slow services
///
/// Cons:
///   ❌ If too aggressive, healthy calls may timeout
///   ❌ Need to define sensible timeout values per service

class TimeoutExample {

    public static String fetchWithTimeout(long timeoutMs) {
        Thread worker = new Thread(() -> {
            try {
                Thread.sleep(3000); // simulate slow service (3s)
            } catch (InterruptedException ignored) {
            }
        });

        worker.start();
        try {
            worker.join(timeoutMs); // wait at most timeoutMs
            if (worker.isAlive()) {
                worker.interrupt();
                return "TIMEOUT: Service did not respond in " + timeoutMs + "ms";
            }
            return "Response received";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Interrupted while waiting";
        }
    }
}


// ═══════════════════════════════════════════════════════════════════════════════
// MAIN – DEMO ALL PATTERNS
// ═══════════════════════════════════════════════════════════════════════════════

public class BuildingResilientSystems {
    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║   BUILDING RESILIENT SYSTEMS – DEMO              ║");
        System.out.println("╚══════════════════════════════════════════════════╝\n");

        // ─── 1. Fallback Demo ─────────────────────────────────────────────
        System.out.println("═══ 1. FALLBACK PATTERN ═══");
        RecommendationService recService = new RecommendationService();

        // Service is down → fallback to cache
        System.out.println("Service DOWN:");
        System.out.println("  Result: " + recService.getRecommendedItems("user-123"));

        // Service comes back up → live data
        recService.setLiveServiceUp(true);
        System.out.println("Service UP:");
        System.out.println("  Result: " + recService.getRecommendedItems("user-123"));
        System.out.println();

        // ─── 2. Naive Retry Demo ──────────────────────────────────────────
        System.out.println("═══ 2. NAIVE RETRY ═══");
        ETAServiceNaiveRetry naiveRetry = new ETAServiceNaiveRetry(3); // succeeds on 3rd attempt
        String etaResult = naiveRetry.getETAWithRetry(5);
        System.out.println("  Final result: " + etaResult);
        System.out.println();

        // ─── 3. Exponential Backoff Demo ──────────────────────────────────
        System.out.println("═══ 3. EXPONENTIAL BACKOFF RETRY ═══");
        ETAServiceBackoffRetry backoffRetry = new ETAServiceBackoffRetry(3);
        String backoffResult = backoffRetry.getETAWithBackoff(5, 200);
        System.out.println("  Final result: " + backoffResult);
        System.out.println();

        // ─── 4. Circuit Breaker Demo ──────────────────────────────────────
        System.out.println("═══ 4. CIRCUIT BREAKER PATTERN ═══");
        // Payment service starts DOWN, threshold=3 failures, open duration=2s
        PaymentService paymentService = new PaymentService(false, 3, 2000);

        // Make calls until circuit opens
        for (int i = 1; i <= 5; i++) {
            System.out.println("Call #" + i + ": "
                    + paymentService.charge("user-A", 99.99));
            System.out.println("  " + paymentService.getCircuitBreaker());
        }

        // Wait for circuit to transition to HALF_OPEN
        System.out.println("\n  Waiting 2.5s for HALF_OPEN transition...");
        try {
            Thread.sleep(2500);
        } catch (InterruptedException ignored) {
        }

        // Service recovers
        paymentService.setServiceUp(true);
        System.out.println("Call #6 (probe): "
                + paymentService.charge("user-A", 49.99));
        System.out.println("  " + paymentService.getCircuitBreaker());
        System.out.println();

        // ─── 5. Timeout Demo ──────────────────────────────────────────────
        System.out.println("═══ 5. TIMEOUT PATTERN ═══");
        System.out.println("  With 1000ms timeout: " + TimeoutExample.fetchWithTimeout(1000));
        System.out.println("  With 5000ms timeout: " + TimeoutExample.fetchWithTimeout(5000));
        System.out.println();

        System.out.println("═══ ALL DEMOS COMPLETE ═══");
    }
}
