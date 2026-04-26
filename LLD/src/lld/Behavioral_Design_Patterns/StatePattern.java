package lld.Behavioral_Design_Patterns;

/// ## State Pattern
/// Behavioral pattern that lets an object alter its behavior when its internal state changes.
/// The object will appear to change its class.
///
/// **Examples:** Vending machines, Document workflow (Draft -> Review -> Published), TCP connections.
///
/// ### The Problem: Tangled State Logic
/// When an object's behavior depends on its state, using large `switch` or `if-else` blocks leads to:
/// - **Fragility:** Adding a new state requires modifying every state-dependent method.
/// - **Poor Readability:** Business logic is buried under conditional checks.
/// - **State Inconsistency:** Transitions between states are hard to manage and validate.

// Legacy Order: State managed via strings and switch statements.
class LegacyOrder {
    private String state = "PLACED";

    public void next() {
        switch (state) {
            case "PLACED":
                state = "PREPARING";
                break;
            case "PREPARING":
                state = "DELIVERING";
                break;
            case "DELIVERING":
                state = "DELIVERED";
                break;
            default:
                System.out.println("Final state reached.");
        }
    }

    public void cancel() {
        if (state.equals("PLACED") || state.equals("PREPARING")) {
            state = "CANCELLED";
            System.out.println("Legacy: Order cancelled.");
        } else {
            System.out.println("Legacy: Cannot cancel now.");
        }
    }
}

/// ### The Solution: State Pattern
/// Encapsulate each state into a separate class. The Context delegates state-specific work to the current state object.
///
/// #### Understanding
/// - **Context (`OrderContext`):** Maintains a reference to a Concrete State object and delegates state-related work.
/// - **State Interface (`OrderState`):** Defines the interface for state-specific behaviors.
/// - **Concrete States (`PlacedState`, `DeliveredState`):** Implement behaviors associated with a state of the Context.
///
/// #### Pros
/// - **Single Responsibility:** Organize code related to particular states into separate classes.
/// - **Open/Closed Principle:** Introduce new states without changing existing state classes or the context.
/// - **Clean Transitions:** Explicitly define which state follows another within the state classes.
///
/// #### Cons
/// - **Class Count:** Can be overkill if the state machine is very simple or rarely changes.

// ========== State Interface ==========

// Defines actions that depend on the order's state.
interface OrderState {
    void next(OrderContext context);

    void cancel(OrderContext context);

    String status();
}

// ========== Context Class ==========

/// Manages the current state and delegates requests.
class OrderContext {
    private OrderState currentState;

    public OrderContext() {
        this.currentState = new PlacedState();
    }

    public void setState(OrderState state) {
        this.currentState = state;
    }

    public void next() {
        currentState.next(this);
    }

    public void cancel() {
        currentState.cancel(this);
    }

    public String getStatus() {
        return currentState.status();
    }
}

// ========== Concrete States ==========

/// State: Initial order placement.
class PlacedState implements OrderState {
    @Override
    public void next(OrderContext ctx) {
        ctx.setState(new PreparingState());
        System.out.println("Transition: Placed -> Preparing");
    }

    @Override
    public void cancel(OrderContext ctx) {
        ctx.setState(new CancelledState());
        System.out.println("Action: Order Cancelled.");
    }

    @Override
    public String status() {
        return "ORDER_PLACED";
    }
}

/// State: Order is being prepared.
class PreparingState implements OrderState {
    @Override
    public void next(OrderContext ctx) {
        ctx.setState(new DeliveredState());
        System.out.println("Transition: Preparing -> Delivered");
    }

    @Override
    public void cancel(OrderContext ctx) {
        ctx.setState(new CancelledState());
        System.out.println("Action: Order Cancelled.");
    }

    @Override
    public String status() {
        return "PREPARING";
    }
}

/// State: Final delivery state (End state).
class DeliveredState implements OrderState {
    @Override
    public void next(OrderContext ctx) {
        System.out.println("Info: Order already delivered.");
    }

    @Override
    public void cancel(OrderContext ctx) {
        System.out.println("Error: Cannot cancel delivered order.");
    }

    @Override
    public String status() {
        return "DELIVERED";
    }
}

/// State: Terminal canceled state.
class CancelledState implements OrderState {
    @Override
    public void next(OrderContext ctx) {
        System.out.println("Error: Cancelled order cannot progress.");
    }

    @Override
    public void cancel(OrderContext ctx) {
        System.out.println("Info: Already cancelled.");
    }

    @Override
    public String status() {
        return "CANCELLED";
    }
}

/// ## Summary of State Pattern
///
/// ### Pros
/// - **Maintainability:** Transitions are easy to visualize and modify.
/// - **No Conditionals:** Replaces complex `switch` logic with polymorphism.
///
/// ### Cons
/// - **Complexity:** Might be complex for simple 2-state machines.
public class StatePattern {
    public static void main(String[] args) {
        // --- PROBLEM CASE ---
        System.out.println("--- Problem: Tangled switch Logic ---");
        LegacyOrder legacy = new LegacyOrder();
        legacy.next();
        legacy.cancel();

        // --- SOLUTION CASE ---
        System.out.println("\n--- Solution: State Pattern ---");
        OrderContext order = new OrderContext();

        System.out.println("Current: " + order.getStatus());
        order.next();   // Placed -> Preparing

        System.out.println("Current: " + order.getStatus());
        order.next();   // Preparing -> Delivered

        System.out.println("Current: " + order.getStatus());
        order.cancel(); // Delivered -> Cannot cancel
    }
}
