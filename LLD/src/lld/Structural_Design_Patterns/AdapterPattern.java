package lld.Structural_Design_Patterns;

/// ## Adapter Pattern
/// Structural pattern that allows objects with incompatible interfaces to collaborate.
/// Acts as a wrapper between two objects.
///
/// **Examples:** Using a USB-to-Ethernet adapter, Legacy API wrappers, Payment gateway integration.
///
/// ### Understanding
/// - **Target:** The interface the client expects.
/// - **Adaptee:** The existing interface that needs adapting (incompatible).
/// - **Adapter:** The class that implements the Target and wraps the Adaptee.
/// - **Client:** The class that uses the Target interface.
///
/// ### Pros
/// - **Single Responsibility:** Interface/Data conversion logic is separated from business logic.
/// - **Open/Closed Principle:** New adapters can be added without breaking existing client code.
/// - **Reusability:** Allows using legacy classes without modification.
///
/// ### Cons
/// - **Complexity:** Increases overall code complexity by introducing new interfaces and classes.

// Target Interface: Standard interface expected by the client.
interface PaymentGateway {
    /// Processes a payment.
    ///
    /// @param orderId Unique order identifier.
    /// @param amount  Total value to pay.
    void pay(String orderId, double amount);
}

/// Concrete implementation of `PaymentGateway` for PayU.
class PayUGateway implements PaymentGateway {
    @Override
    public void pay(String orderId, double amount) {
        System.out.println("Paid Rs." + amount + " using PayU for order: " + orderId);
    }
}

/// Adaptee: An existing class with an incompatible interface.
class RazorpayAPI {
    /// Legacy/Third-party method for payment.
    public void makePayment(String invoiceId, double amountInRupees) {
        System.out.println("Paid Rs." + amountInRupees + " using Razorpay for invoice: " + invoiceId);
    }
}

/// Adapter Class: Bridges `RazorpayAPI` to `PaymentGateway`.
class RazorpayAdapter implements PaymentGateway {
    private final RazorpayAPI razorpayAPI;

    public RazorpayAdapter() {
        this.razorpayAPI = new RazorpayAPI();
    }

    /// Translates the `pay()` call to Razorpay's `makePayment()` method.
    @Override
    public void pay(String orderId, double amount) {
        razorpayAPI.makePayment(orderId, amount);
    }
}

/// Client Class: Uses `PaymentGateway` interface to process payments.
class CheckoutService {
    private final PaymentGateway paymentGateway;

    /// Constructor injection for dependency inversion.
    public CheckoutService(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    /// Business logic to perform checkout.
    public void checkout(String orderId, double amount) {
        paymentGateway.pay(orderId, amount);
    }
}

/// ## Summary of Adapter Pattern
///
/// ### Pros
/// - **Compatibility:** Connects disparate systems without code changes to Adaptee.
///
/// ### Cons
/// - **Overhead:** Sometimes simpler to just change the Adaptee if you own the source code.
public class AdapterPattern {
    public static void main(String[] args) {
        // Using Razorpay via Adapter
        CheckoutService razorPayCheckout = new CheckoutService(new RazorpayAdapter());
        razorPayCheckout.checkout("12", 1780);

        // Using PayU directly (implements Target)
        CheckoutService payUCheckout = new CheckoutService(new PayUGateway());
        payUCheckout.checkout("13", 500);
    }
}
