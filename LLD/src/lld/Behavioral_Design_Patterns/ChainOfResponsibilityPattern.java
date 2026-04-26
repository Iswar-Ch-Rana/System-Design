package lld.Behavioral_Design_Patterns;

/// ## Chain of Responsibility Pattern
/// Behavioral pattern that lets you pass requests along a chain of handlers.
/// Upon receiving a request, each handler decides either to process the request or to pass it to the next handler in the chain.
///
/// **Examples:** Middleware in web frameworks, Logger levels, Multi-level support systems, Event bubbling.
///
/// ### The Problem: Bloated Dispatcher
/// A central service must know about every possible request type and its handler.
/// This leads to a massive, fragile `if-else` or `switch` block that is hard to maintain and extend.

// Legacy Support: Centralized if-else logic.
class LegacySupportService {
    public void handleRequest(String type) {
        if (type.equals("billing")) {
            System.out.println("Legacy: Billing team handles refund.");
        } else if (type.equals("tech")) {
            System.out.println("Legacy: Tech team handles bug.");
        } else {
            System.out.println("Legacy: No handler found.");
        }
    }
}

/// ### The Solution: Chain of Responsibility
/// Decouple the sender and receiver by giving multiple objects a chance to handle the request.
///
/// #### Understanding
/// - **Handler Interface (`SupportHandler`):** Declares the interface for all concrete handlers. Usually contains a `setNext()` method.
/// - **Base Handler:** Optional class to store a reference to the next handler and implement default behavior.
/// - **Concrete Handlers (`BillingSupport`, `TechnicalSupport`):** Contain the actual code for processing the request.
/// - **Client:** Composes the chain and initiates the request to the first handler.
///
/// #### Pros
/// - **Single Responsibility:** Each class handles one type of request.
/// - **Open/Closed Principle:** Add new handlers without changing the client or existing handlers.
/// - **Flexibility:** Can change the order of the chain at runtime.
///
/// #### Cons
/// - **No Guarantee:** A request might fall off the end of the chain if no handler processes it.
/// - **Debugging:** Can be hard to track which handler processed a specific request.

// ========== Abstract Handler ==========

// Base class for all support levels.
abstract class SupportHandler {
    protected SupportHandler next;

    /// Sets the next handler in the chain.
    public void setNext(SupportHandler next) {
        this.next = next;
    }

    /// Handles the request or passes it to the next.
    public abstract void handle(String requestType);
}

// ========== Concrete Handlers ==========

/// Handles billing and refund queries.
class BillingSupport extends SupportHandler {
    @Override
    public void handle(String requestType) {
        if (requestType.equalsIgnoreCase("billing")) {
            System.out.println("BillingSupport: Processing refund request.");
        } else if (next != null) {
            next.handle(requestType);
        }
    }
}

/// Handles technical issues and bugs.
class TechnicalSupport extends SupportHandler {
    @Override
    public void handle(String requestType) {
        if (requestType.equalsIgnoreCase("tech")) {
            System.out.println("TechnicalSupport: Fixing software bug.");
        } else if (next != null) {
            next.handle(requestType);
        }
    }
}

/// Handles logistics and delivery queries.
class DeliverySupport extends SupportHandler {
    @Override
    public void handle(String requestType) {
        if (requestType.equalsIgnoreCase("delivery")) {
            System.out.println("DeliverySupport: Tracking shipment.");
        } else if (next != null) {
            next.handle(requestType);
        } else {
            System.out.println("DeliverySupport: End of chain. No handler found for: " + requestType);
        }
    }
}

/// ## Summary of Chain of Responsibility Pattern
///
/// ### Pros
/// - **Reduced Coupling:** Sender doesn't need to know which object handles the request.
///
/// ### Cons
/// - **Performance:** Long chains can lead to slight performance degradation.
public class ChainOfResponsibilityPattern {
    public static void main(String[] args) {
        // --- PROBLEM CASE ---
        System.out.println("--- Problem: Bloated if-else ---");
        LegacySupportService legacy = new LegacySupportService();
        legacy.handleRequest("billing");

        // --- SOLUTION CASE ---
        System.out.println("\n--- Solution: Chain of Responsibility ---");

        // 1. Initialize handlers
        SupportHandler billing = new BillingSupport();
        SupportHandler tech = new TechnicalSupport();
        SupportHandler delivery = new DeliverySupport();

        // 2. Build the chain
        billing.setNext(tech);
        tech.setNext(delivery);

        // 3. Process requests
        System.out.println("Request: 'tech'");
        billing.handle("tech");

        System.out.println("\nRequest: 'delivery'");
        billing.handle("delivery");

        System.out.println("\nRequest: 'unknown'");
        billing.handle("unknown");
    }
}
