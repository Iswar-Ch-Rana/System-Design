package lld.Creational_Design_Patterns;

import java.util.HashMap;
import java.util.Map;

/// ## Prototype Pattern
/// Allows cloning existing objects without coupling code to their specific classes.
///
/// **Examples:** Game character spawning, Document templates, UI component duplication.
///
/// ### Understanding
/// - **Cloning:** Creates a copy of an existing object.
/// - **Registry:** Often uses a "template manager" to store and retrieve pre-configured objects.
/// - **Performance:** Faster than `new` if object initialization is heavy (e.g., DB fetch).
///
/// ### Pros
/// - **Performance:** Reduces overhead of complex object creation.
/// - **Dynamic Configuration:** Can clone pre-configured instances at runtime.
/// - **Avoids Subclassing:** No need for a factory for every product type.
///
/// ### Cons
/// - **Deep Copy Complexity:** Cloning objects with circular references or complex graphs is difficult.
/// - **Boilerplate:** Every class must implement a clone method.

// Defining the Prototype Interface.
interface EmailTemplate extends Cloneable {
    /// Recommended to perform deep copy to ensure independence.
    EmailTemplate clone();

    void setContent(String content);

    void send(String to);
}

/// Concrete Class implementing clone logic.
class WelcomeEmail implements EmailTemplate {
    private final String subject;
    private String content;

    public WelcomeEmail() {
        this.subject = "Welcome to YouTube";
        this.content = "Hi there! Thanks for joining us.";
    }

    @Override
    public WelcomeEmail clone() {
        try {
            // Shallow copy works here because fields are Strings (immutable)
            return (WelcomeEmail) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone failed", e);
        }
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public void send(String to) {
        System.out.println("Sending to " + to + ": [" + subject + "] " + content);
    }
}

/// Template Registry to store and provide clones.
class EmailTemplateRegistry {
    private static final Map<String, EmailTemplate> templates = new HashMap<>();

    static {
        // Pre-configured "Prototypes"
        templates.put("welcome", new WelcomeEmail());
    }

    /// Fetches a clone of the requested template.
    ///
    /// @param type Template key.
    /// @return A fresh clone of the original template.
    public static EmailTemplate getTemplate(String type) {
        // Clone to avoid modifying the original prototype in the registry
        return templates.get(type).clone();
    }
}

/// ## Summary of Prototype Pattern
///
/// ### Pros
/// - **Resource Efficiency:** Reuse existing state instead of re-calculating.
/// - **Runtime Flexibility:** Add/Remove prototypes dynamically.
///
/// ### Cons
/// - **Implementation Overhead:** Java's `Cloneable` is often considered flawed; might require custom copy constructors.
public class PrototypePattern {
    public static void main(String[] args) {
        // Fetch from registry
        EmailTemplate welcomeEmail1 = EmailTemplateRegistry.getTemplate("welcome");
        welcomeEmail1.setContent("Hi Alice, welcome to YouTube Premium!");
        welcomeEmail1.send("alice@example.com");

        EmailTemplate welcomeEmail2 = EmailTemplateRegistry.getTemplate("welcome");
        welcomeEmail2.setContent("Hi Bob, thanks for joining!");
        welcomeEmail2.send("bob@example.com");

        // welcomeEmail1 and welcomeEmail2 are independent copies of the "welcome" prototype
    }
}
