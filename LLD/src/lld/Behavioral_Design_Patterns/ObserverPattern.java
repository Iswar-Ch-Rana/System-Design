package lld.Behavioral_Design_Patterns;

import java.util.ArrayList;
import java.util.List;

/// ## Observer Pattern
/// Behavioral pattern that defines a one-to-many dependency between objects
/// so that when one object changes state, all its dependents are notified automatically.
///
/// **Examples:** Event listeners (DOM events), Pub/Sub systems, Stocks price alerts.
///
/// ### The Problem: Hardcoded Dependencies
/// The Subject (Channel) must manually manage and notify specific observers.
/// Adding a new notification type (e.g., SMS) requires modifying the Subject class.

// Legacy Channel: Hardcoded notification logic.
class LegacyYouTubeChannel {
    public void uploadNewVideo(String videoTitle) {
        System.out.println("Uploading: " + videoTitle);
        // Hardcoded: Subject knows about specific delivery methods
        System.out.println("Sending email to user1@example.com");
        System.out.println("Pushing in-app notification to user3");
    }
}

/// ### The Solution: Observer Pattern
/// The Subject maintains a list of interested Observers and notifies them via a common interface.
///
/// #### Understanding
/// - **Subject (`Channel`):** Keeps a list of observers and provides methods to attach/detach them.
/// - **Observer (`Subscriber`):** Interface for objects that should be notified of changes.
/// - **Concrete Subject (`YouTubeChannel`):** Sends notifications to observers when state changes.
/// - **Concrete Observer (`EmailSubscriber`, `MobileAppSubscriber`):** Specific reaction to notifications.
///
/// #### Pros
/// - **Loose Coupling:** Subject doesn't know the concrete classes of observers.
/// - **Open/Closed Principle:** Add new subscribers without changing the Subject's code.
/// - **Broadcast Communication:** Notifications are sent automatically to all registered observers.
///
/// #### Cons
/// - **Order:** Observers are notified in random order.
/// - **Memory Leaks:** Observers must be detached when no longer needed to prevent memory leaks.

// ========== Observer Interfaces ==========

// Observer Interface.
interface Subscriber {
    void update(String channelName, String videoTitle);
}

/// Subject Interface.
interface Channel {
    void subscribe(Subscriber subscriber);

    void unsubscribe(Subscriber subscriber);

    void notifySubscribers(String videoTitle);
}

// ========== Concrete Implementation ==========

/// Concrete Subject.
class YouTubeChannel implements Channel {
    private final List<Subscriber> subscribers = new ArrayList<>();
    private final String channelName;

    public YouTubeChannel(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public void subscribe(Subscriber s) {
        subscribers.add(s);
    }

    @Override
    public void unsubscribe(Subscriber s) {
        subscribers.remove(s);
    }

    @Override
    public void notifySubscribers(String videoTitle) {
        for (Subscriber s : subscribers) {
            s.update(channelName, videoTitle);
        }
    }

    /// Triggers the notification process.
    public void uploadVideo(String videoTitle) {
        System.out.println("\n[" + channelName + "] Uploading video: " + videoTitle);
        notifySubscribers(videoTitle);
    }
}

/// Concrete Observer: Email.
class EmailSubscriber implements Subscriber {
    private final String email;

    public EmailSubscriber(String email) {
        this.email = email;
    }

    @Override
    public void update(String channel, String video) {
        System.out.println("Email -> " + email + ": New video on " + channel + " -> " + video);
    }
}

/// Concrete Observer: Mobile App.
class MobileAppSubscriber implements Subscriber {
    private final String username;

    public MobileAppSubscriber(String username) {
        this.username = username;
    }

    @Override
    public void update(String channel, String video) {
        System.out.println("Push -> " + username + ": " + channel + " just uploaded " + video);
    }
}

/// ## Summary of Observer Pattern
///
/// ### Pros
/// - **Relationship Flexibility:** Can add/remove observers at runtime.
///
/// ### Cons
/// - **Performance:** If there are too many observers, notifying all can be slow.
public class ObserverPattern {
    public static void main(String[] args) {
        // --- PROBLEM CASE ---
        System.out.println("--- Problem: Hardcoded Notifications ---");
        LegacyYouTubeChannel legacy = new LegacyYouTubeChannel();
        legacy.uploadNewVideo("Observer Pattern Explained");

        // --- SOLUTION CASE ---
        System.out.println("\n--- Solution: Observer Pattern ---");
        YouTubeChannel youTubeChannel = new YouTubeChannel("TechExplained");

        // Dynamic subscription
        youTubeChannel.subscribe(new MobileAppSubscriber("user123"));
        youTubeChannel.subscribe(new EmailSubscriber("fan@example.com"));

        // Broadcaster notifies everyone automatically
        youTubeChannel.uploadVideo("DP Series: Part 1");

        // Can add more observers without touching YouTubeChannel class
        youTubeChannel.subscribe(new EmailSubscriber("new_user@test.com"));
        youTubeChannel.uploadVideo("DP Series: Part 2");
    }
}
