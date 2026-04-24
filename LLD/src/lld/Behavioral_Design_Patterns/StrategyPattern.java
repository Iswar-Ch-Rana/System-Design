package lld.Behavioral_Design_Patterns;

/// ## Strategy Pattern
/// Behavioral pattern that defines a family of algorithms, encapsulates each one,
/// and makes them interchangeable at runtime.
///
/// **Examples:** Payment methods (Credit Card, PayPal, Crypto), Sorting algorithms, Compression types.
///
/// ### The Problem: Rigid Conditional Logic
/// When multiple algorithms are used for the same task, hardcoding them inside `if-else` or `switch` blocks leads to:
/// - **Tightly Coupled Code:** Adding a new strategy requires modifying the core service.
/// - **Testing Difficulty:** Logic is mixed together, making unit tests complex.
/// - **Maintenance Burden:** The service class grows indefinitely with every new requirement.

// Legacy Service: Uses hardcoded logic.
class LegacyRideMatchingService {
    public void matchRider(String riderLocation, String matchingType) {
        if (matchingType.equals("NEAREST")) {
            System.out.println("Legacy: Matching " + riderLocation + " with nearest driver.");
        } else if (matchingType.equals("SURGE")) {
            System.out.println("Legacy: Matching " + riderLocation + " with surge priority.");
        } else {
            System.out.println("Legacy: Invalid strategy.");
        }
    }
}

/// ### The Solution: Strategy Pattern
/// Extract algorithms into separate classes (strategies) that implement a common interface.
///
/// #### Understanding
/// - **Strategy Interface (`MatchingStrategy`):** Common interface for all supported algorithms.
/// - **Concrete Strategy (`NearestDriverStrategy`, `SurgePriorityStrategy`):** Specific algorithm implementations.
/// - **Context (`RideMatchingService`):** Maintains a reference to a Strategy object and delegates work to it.
///
/// #### Pros
/// - **Open/Closed Principle:** Add new strategies without changing the Context.
/// - **Clean Code:** Replaces massive conditional blocks with isolated classes.
/// - **Runtime Flexibility:** Can swap algorithms while the application is running.
///
/// #### Cons
/// - **Complexity:** Increases number of classes.
/// - **Client Knowledge:** Client must be aware of different strategies to select the right one.

// ========== Strategy Interface ==========

// Common interface for all ride matching algorithms.
interface MatchingStrategy {
    void match(String riderLocation);
}

// ========== Concrete Strategies ==========

/// Algorithm: Find the closest available driver.
class NearestDriverStrategy implements MatchingStrategy {
    @Override
    public void match(String riderLocation) {
        System.out.println("Strategy -> Nearest: Finding closest driver to " + riderLocation);
    }
}

/// Algorithm: Match based on airport FIFO queue.
class AirportQueueStrategy implements MatchingStrategy {
    @Override
    public void match(String riderLocation) {
        System.out.println("Strategy -> Airport: Picking first driver from queue at " + riderLocation);
    }
}

/// Algorithm: Prioritize drivers in high-surge zones.
class SurgePriorityStrategy implements MatchingStrategy {
    @Override
    public void match(String riderLocation) {
        System.out.println("Strategy -> Surge: Matching based on pricing priority near " + riderLocation);
    }
}

// ========== Context Class ==========

/// The service that uses a Strategy to perform matching.
class RideMatchingService {
    private MatchingStrategy strategy;

    /// Constructor injection.
    public RideMatchingService(MatchingStrategy strategy) {
        this.strategy = strategy;
    }

    /// Setter injection for dynamic switching.
    public void setStrategy(MatchingStrategy strategy) {
        this.strategy = strategy;
    }

    /// Delegates matching logic to the current strategy.
    public void matchRider(String location) {
        strategy.match(location);
    }
}

/// ## Summary of Strategy Pattern
///
/// ### Pros
/// - **Isolation:** Algorithm details are hidden from the context class.
/// - **Testing:** Each strategy can be tested independently.
///
/// ### Cons
/// - **Overhead:** Communication between Context and Strategy might add slight overhead.
public class StrategyPattern {
    public static void main(String[] args) {
        // --- PROBLEM CASE ---
        System.out.println("--- Problem: Rigid Conditional Logic ---");
        LegacyRideMatchingService legacy = new LegacyRideMatchingService();
        legacy.matchRider("Downtown", "NEAREST");

        // --- SOLUTION CASE ---
        System.out.println("\n--- Solution: Strategy Pattern ---");

        // Start with Nearest Driver logic
        RideMatchingService service = new RideMatchingService(new NearestDriverStrategy());
        service.matchRider("City Center");

        // Dynamically switch to Surge Priority at peak hours
        System.out.println("Swapping strategy to Surge Priority...");
        service.setStrategy(new SurgePriorityStrategy());
        service.matchRider("City Center");

        // Switch to Airport Queue for pickups
        System.out.println("Swapping strategy to Airport Queue...");
        service.setStrategy(new AirportQueueStrategy());
        service.matchRider("Terminal 1");
    }
}
