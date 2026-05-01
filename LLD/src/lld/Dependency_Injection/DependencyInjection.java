package lld.Dependency_Injection;

/// =================== Dependency Injection (DI) ===================

/// What is DI?
/// A design pattern where objects receive their dependencies from external sources
/// rather than creating them internally.
///
/// Benefits: ✓ Loose coupling ✓ Easy testing ✓ Flexibility ✓ Better maintainability
/// Types: Constructor, Setter, Interface Injection
//
/// =================== Problem: Tight Coupling ===================

/// BAD: OrderService creates its own dependencies
class TightlyCoupledOrderService {
    private final PaymentService payment = new RazorpayPayment();
    /// Hard-coded dependency
    private final NotificationService notification = new EmailNotification();

    public void checkout(Order order) {
        payment.process(order);
        notification.send("Order confirmed: " + order.id());
    }
}

/// Problems:
/// ✗ Hard to test (can't mock RazorpayPayment)
/// ✗ Hard to change (stuck with Razorpay)
/// ✗ Violates SOLID principles (high coupling)

/// =================== 1. Constructor Injection (Recommended) ===================

/// PROS: ✓ Immutable ✓ Clear dependencies ✓ Compile-time safety ✓ Easy to test
/// CONS: ✗ Can have many parameters ✗ Not suitable for optional dependencies

/// GOOD: Dependencies injected via constructor
class ConstructorInjectedOrderService {
    private final PaymentService payment;
    private final NotificationService notification;

    public ConstructorInjectedOrderService(PaymentService payment, NotificationService notification) {
        this.payment = payment;
        this.notification = notification;
    }

    public void checkout(Order order) {
        payment.process(order);
        notification.send("Order confirmed: " + order.id());
    }
}

/// =================== 2. Setter Injection ===================

/// PROS: ✓ Optional dependencies ✓ Can change at runtime ✓ Flexible
/// CONS: ✗ Mutable state ✗ Can forget to set ✗ NullPointerException risk

class SetterInjectedOrderService {
    private PaymentService payment;
    private NotificationService notification;

    public void setPayment(PaymentService payment) {
        this.payment = payment;
    }

    public void setNotification(NotificationService notification) {
        this.notification = notification;
    }

    public void checkout(Order order) {
        if (payment == null) throw new IllegalStateException("Payment not set");
        payment.process(order);
        if (notification != null) notification.send("Order confirmed");
    }
}

/// =================== 3. Interface Injection ===================

/// PROS: ✓ Explicit contract ✓ Framework-driven
/// CONS: ✗ More boilerplate ✗ Rarely used ✗ Overkill for simple cases

interface PaymentInjectable {
    void injectPayment(PaymentService payment);
}

class InterfaceInjectedOrderService implements PaymentInjectable {
    private PaymentService payment;

    @Override
    public void injectPayment(PaymentService payment) {
        this.payment = payment;
    }

    public void checkout(Order order) {
        payment.process(order);
    }
}

/// =================== Dependencies (Interfaces & Implementations) ===================

interface PaymentService {
    void process(Order order);
}

class RazorpayPayment implements PaymentService {
    @Override
    public void process(Order order) {
        System.out.println("Processing payment via Razorpay for order: " + order.id());
    }
}

class StripePayment implements PaymentService {
    @Override
    public void process(Order order) {
        System.out.println("Processing payment via Stripe for order: " + order.id());
    }
}

interface NotificationService {
    void send(String message);
}

class EmailNotification implements NotificationService {
    @Override
    public void send(String message) {
        System.out.println("Email: " + message);
    }
}

class SMSNotification implements NotificationService {
    @Override
    public void send(String message) {
        System.out.println("SMS: " + message);
    }
}

/// Simple order model
record Order(String id, double amount) {
}


/// =================== Comparison ===================

/// Type                | When to Use                           | Thread-Safe | Immutable
/// --------------------|---------------------------------------|-------------|----------
/// Constructor         | Required dependencies                 | Yes         | Yes
/// Setter              | Optional dependencies                 | No          | No
/// Interface           | Framework-driven (Spring, Guice)      | Depends     | Depends
///
/// Best Practice: Prefer Constructor Injection for required dependencies

/// =================== Main ===================

public class DependencyInjection {
    public static void main(String[] args) {
        Order order = new Order("ORD-123", 1500.0);

        System.out.println("=== Demo 1: Tight Coupling (BAD) ===\n");
        TightlyCoupledOrderService badService = new TightlyCoupledOrderService();
        badService.checkout(order);

        System.out.println("\n=== Demo 2: Constructor Injection (GOOD) ===\n");
        PaymentService razorpay = new RazorpayPayment();
        NotificationService email = new EmailNotification();
        ConstructorInjectedOrderService constructorService = new ConstructorInjectedOrderService(razorpay, email);
        constructorService.checkout(order);

        System.out.println("\n=== Demo 3: Easy to Switch Dependencies ===\n");
        PaymentService stripe = new StripePayment();
        NotificationService sms = new SMSNotification();
        ConstructorInjectedOrderService flexibleService = new ConstructorInjectedOrderService(stripe, sms);
        flexibleService.checkout(order);

        System.out.println("\n=== Demo 4: Setter Injection ===\n");
        SetterInjectedOrderService setterService = new SetterInjectedOrderService();
        setterService.setPayment(razorpay);
        setterService.setNotification(email);
        setterService.checkout(order);

        System.out.println("\n=== Demo 5: Interface Injection ===\n");
        InterfaceInjectedOrderService interfaceService = new InterfaceInjectedOrderService();
        interfaceService.injectPayment(stripe);
        interfaceService.checkout(order);
    }
}
