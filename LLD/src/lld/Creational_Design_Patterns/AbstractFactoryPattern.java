package lld.Creational_Design_Patterns;

/// ## Abstract Factory Pattern
/// Provides an interface for creating families of related or dependent objects
/// without specifying their concrete classes.
///
/// **Examples:** UI toolkits for different OS (Mac, Win), Regional settings (VAT, Currency).
///
/// ### Understanding
/// - **Encapsulation:** Factory of factories.
/// - **Family Consistency:** Ensures that products from the same factory work together.
/// - **Decoupling:** Client is agnostic of the concrete factory.
///
/// ### Pros
/// - **Consistency:** Ensures related products are created from the same factory.
/// - **Loose Coupling:** Client code stays independent of specific classes.
/// - **Single Responsibility:** Creation logic is isolated in factories.
///
/// ### Cons
/// - **Complexity:** High abstraction level makes code harder to follow.
/// - **Rigidity:** Adding a new product requires changing the abstract factory and all concrete factories.

// ========== Interfaces ==========

// Common interface for processing payments.
interface PaymentGateway {
    void processPayment(double amount);
}

/// Common interface for generating invoices.
interface Invoice {
    void generateInvoice();
}

// ========== India Implementations ==========

class RazorpayGateway implements PaymentGateway {
    public void processPayment(double amount) {
        System.out.println("Processing INR payment via Razorpay: " + amount);
    }
}

class PayUGateway implements PaymentGateway {
    public void processPayment(double amount) {
        System.out.println("Processing INR payment via PayU: " + amount);
    }
}

class GSTInvoice implements Invoice {
    public void generateInvoice() {
        System.out.println("Generating GST Invoice for India.");
    }
}

// ========== US Implementations ==========

class PayPalGateway implements PaymentGateway {
    public void processPayment(double amount) {
        System.out.println("Processing USD payment via PayPal: " + amount);
    }
}

class StripeGateway implements PaymentGateway {
    public void processPayment(double amount) {
        System.out.println("Processing USD payment via Stripe: " + amount);
    }
}

class USInvoice implements Invoice {
    public void generateInvoice() {
        System.out.println("Generating Invoice as per US norms.");
    }
}

// ========== Abstract Factory ==========

/// Abstract Factory interface for regional resources.
interface RegionFactory {
    PaymentGateway createPaymentGateway(String gatewayType);

    Invoice createInvoice();
}

// ========== Concrete Factories ==========

class IndiaFactory implements RegionFactory {
    @Override
    public PaymentGateway createPaymentGateway(String gatewayType) {
        if (gatewayType.equalsIgnoreCase("razorpay")) {
            return new RazorpayGateway();
        } else if (gatewayType.equalsIgnoreCase("payu")) {
            return new PayUGateway();
        }
        throw new IllegalArgumentException("Unsupported gateway for India: " + gatewayType);
    }

    @Override
    public Invoice createInvoice() {
        return new GSTInvoice();
    }
}

class USFactory implements RegionFactory {
    @Override
    public PaymentGateway createPaymentGateway(String gatewayType) {
        if (gatewayType.equalsIgnoreCase("paypal")) {
            return new PayPalGateway();
        } else if (gatewayType.equalsIgnoreCase("stripe")) {
            return new StripeGateway();
        }
        throw new IllegalArgumentException("Unsupported gateway for US: " + gatewayType);
    }

    @Override
    public Invoice createInvoice() {
        return new USInvoice();
    }
}

// ========== Checkout Service ==========

class CheckoutService {
    private final PaymentGateway paymentGateway;
    private final Invoice invoice;

    public CheckoutService(RegionFactory factory, String gatewayType) {
        this.paymentGateway = factory.createPaymentGateway(gatewayType);
        this.invoice = factory.createInvoice();
    }

    public void completeOrder(double amount) {
        paymentGateway.processPayment(amount);
        invoice.generateInvoice();
    }
}

/// ## Summary of Abstract Factory Pattern
///
/// ### Pros
/// - **Scalability:** Easy to add new regions (factories).
/// - **Interchangeability:** Can switch between factories at runtime.
///
/// ### Cons
/// - **Extension Overhead:** Adding a new product type is expensive.
public class AbstractFactoryPattern {
    public static void main(String[] args) {
        CheckoutService indiaCheckout = new CheckoutService(new IndiaFactory(), "razorpay");
        indiaCheckout.completeOrder(1999.0);

        System.out.println("---");

        CheckoutService usCheckout = new CheckoutService(new USFactory(), "paypal");
        usCheckout.completeOrder(49.99);
    }
}
