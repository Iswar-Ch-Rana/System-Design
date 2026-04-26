package lld.Behavioral_Design_Patterns;

/// ## Template Method Pattern
/// Behavioral pattern that defines the skeleton of an algorithm in the superclass
/// but lets subclasses override specific steps of the algorithm without changing its structure.
///
/// **Examples:** Build tools (compile -> test -> package), Data miners, UI framework life-cycles.
///
/// ### The Problem: Code Duplication
/// Multiple classes perform similar tasks with a few differing steps. 
/// Repeating common logic (logging, validation, rate-limiting) leads to maintenance nightmares 
/// and inconsistent behavior across implementations.

// Legacy Email: Duplicate validation and logging.
class LegacyEmailSender {
    public void send(String to, String msg) {
        System.out.println("Validating: " + to);
        System.out.println("Audit log: " + msg);
        System.out.println("Sending Email: <html>" + msg + "</html>");
    }
}

/// Legacy SMS: Identical validation and logging logic repeated.
class LegacySMSSender {
    public void send(String to, String msg) {
        System.out.println("Validating: " + to);
        System.out.println("Audit log: " + msg);
        System.out.println("Sending SMS: [SMS] " + msg);
    }
}

/// ### The Solution: Template Pattern
/// Push common steps to a base class and define "hooks" or abstract methods for variant steps.
///
/// #### Understanding
/// - **Abstract Class (`NotificationSender`):** Defines the `templateMethod()` that fixes the algorithm's structure.
/// - **Template Method (`send()`):** A `final` method that calls steps in a specific order.
/// - **Abstract Steps (`composeMessage`, `sendMessage`):** Steps that MUST be implemented by subclasses.
/// - **Hooks (`postSendAnalytics`):** Optional steps with default implementations.
///
/// #### Pros
/// - **Code Reuse:** Centralize common code in the superclass.
/// - **Controlled Extension:** Subclasses can only override specific points in the algorithm.
///
/// #### Cons
/// - **Rigidity:** Some clients might find the fixed skeleton too restrictive.
/// - **Liskov Substitution:** Violates principle if subclasses try to change behavior of base steps.

// ========== Abstract Template ==========

// Base class defining the notification algorithm.
abstract class NotificationSender {

    /// The Template Method. Fixed algorithm structure.
    public final void send(String to, String rawMessage) {
        rateLimitCheck(to);
        validateRecipient(to);
        String formatted = formatMessage(rawMessage);

        // Variant parts delegated to subclasses
        String composed = composeMessage(formatted);
        sendMessage(to, composed);

        // Optional hook
        postSendAnalytics(to);
    }

    private void rateLimitCheck(String to) {
        System.out.println("[Common] Checking rate limit for " + to);
    }

    private void validateRecipient(String to) {
        System.out.println("[Common] Validating " + to);
    }

    private String formatMessage(String msg) {
        return msg.trim();
    }

    /// Abstract step: Subclasses must implement.
    protected abstract String composeMessage(String formatted);

    /// Abstract step: Subclasses must implement.
    protected abstract void sendMessage(String to, String msg);

    /// Hook: Optional to override.
    protected void postSendAnalytics(String to) {
        System.out.println("[Common] Updating generic analytics.");
    }
}

// ========== Concrete Implementations ==========

class EmailSender extends NotificationSender {
    @Override
    protected String composeMessage(String msg) {
        return "<html>" + msg + "</html>";
    }

    @Override
    protected void sendMessage(String to, String msg) {
        System.out.println("EMAIL Service: Delivering to " + to);
    }
}

class SMSSender extends NotificationSender {
    @Override
    protected String composeMessage(String msg) {
        return "[SMS] " + msg;
    }

    @Override
    protected void sendMessage(String to, String msg) {
        System.out.println("SMS Service: Sending to " + to);
    }

    @Override
    protected void postSendAnalytics(String to) {
        System.out.println("[SMS] Custom carrier-based analytics updated.");
    }
}

/// ## Summary of Template Pattern
///
/// ### Pros
/// - **Consistency:** Every notification type follows the same security and logging rules.
///
/// ### Cons
/// - **Maintenance:** Harder to maintain as the number of steps in the template grows.
public class TemplatePattern {
    public static void main(String[] args) {
        // --- PROBLEM CASE ---
        System.out.println("--- Problem: Duplicate Logic ---");
        new LegacyEmailSender().send("test@abc.com", "Hi");
        new LegacySMSSender().send("999", "Hi");

        // --- SOLUTION CASE ---
        System.out.println("\n--- Solution: Template Method Pattern ---");

        NotificationSender email = new EmailSender();
        email.send("john@doe.com", " Order Placed ");

        System.out.println();

        NotificationSender sms = new SMSSender();
        sms.send("555-0199", " OTP: 1234 ");
    }
}
