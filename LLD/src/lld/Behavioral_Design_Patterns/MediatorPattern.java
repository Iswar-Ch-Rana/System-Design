package lld.Behavioral_Design_Patterns;

import java.util.ArrayList;
import java.util.List;

/// ## Mediator Pattern
/// Behavioral pattern that reduces chaotic dependencies between objects.
/// It restricts direct communications between objects and forces them to collaborate only via a mediator object.
///
/// **Examples:** Air Traffic Control (ATC), Chat rooms, GUI dialogs (interaction between buttons/inputs).
///
/// ### The Problem: Chaotic M:N Coupling
/// When objects communicate directly with each other, every object must maintain a reference to all others.
/// As the number of objects grows, the number of connections increases exponentially (M * N),
/// making the system fragile and hard to modify.

// Legacy User: Stores references to all other users.
class LegacyUser {
    private final String name;
    private final List<LegacyUser> others = new ArrayList<>();

    public LegacyUser(String name) {
        this.name = name;
    }

    /// Manual connection leads to tight coupling.
    public void addCollaborator(LegacyUser user) {
        others.add(user);
    }

    public void sendMessage(String msg) {
        System.out.println(name + " sends: " + msg);
        for (LegacyUser u : others) {
            u.receive(msg, this.name);
        }
    }

    public void receive(String msg, String from) {
        System.out.println(name + " received: [" + msg + "] from " + from);
    }
}

/// ### The Solution: Mediator Pattern
/// Introduce a central "Mediator" that handles all communication. Objects only know about the Mediator.
///
/// #### Understanding
/// - **Mediator Interface (`ChatMediator`):** Declares methods for communication.
/// - **Concrete Mediator (`ChatRoom`):** Manages coordination between colleague objects.
/// - **Colleague Class (`User`):** Participants that communicate only via the mediator.
///
/// #### Pros
/// - **Decoupling:** Objects don't need to know about each other.
/// - **Centralization:** Interaction logic is in one place (the mediator).
/// - **Simplification:** M:N connections are replaced by 1:M connections.
///
/// #### Cons
/// - **God Object:** The mediator can become overly complex over time.
/// - **Indirection:** Harder to follow the flow of data at a glance.

// ========== Mediator Interface ==========

// Interface defining the communication protocol.
interface ChatMediator {
    void sendMessage(String msg, User user);

    void addUser(User user);
}

// ========== Colleague Class ==========

/// Abstract participant in the system.
abstract class User {
    protected ChatMediator mediator;
    protected String name;

    public User(ChatMediator mediator, String name) {
        this.mediator = mediator;
        this.name = name;
    }

    public abstract void send(String msg);

    public abstract void receive(String msg);
}

// ========== Concrete Implementation ==========

/// Concrete Mediator.
class ChatRoom implements ChatMediator {
    private final List<User> users = new ArrayList<>();

    @Override
    public void addUser(User user) {
        users.add(user);
    }

    @Override
    public void sendMessage(String msg, User sender) {
        for (User u : users) {
            // Mediator decides who receives the message (not the sender)
            if (u != sender) {
                u.receive(msg);
            }
        }
    }
}

/// Concrete Colleague.
class ChatUser extends User {
    public ChatUser(ChatMediator mediator, String name) {
        super(mediator, name);
    }

    @Override
    public void send(String msg) {
        System.out.println(name + " sending message...");
        mediator.sendMessage(msg, this);
    }

    @Override
    public void receive(String msg) {
        System.out.println(name + " received: " + msg);
    }
}

/// ## Summary of Mediator Pattern
///
/// ### Pros
/// - **Ease of Maintenance:** Changing interaction logic only requires updating the Mediator.
///
/// ### Cons
/// - **Bottleneck:** All traffic goes through one object; might impact performance in massive systems.
public class MediatorPattern {
    public static void main(String[] args) {
        // --- PROBLEM CASE ---
        System.out.println("--- Problem: Tight M:N Coupling ---");
        LegacyUser alice = new LegacyUser("Alice");
        LegacyUser bob = new LegacyUser("Bob");
        alice.addCollaborator(bob);
        bob.addCollaborator(alice);
        alice.sendMessage("Hello Bob!");

        // --- SOLUTION CASE ---
        System.out.println("\n--- Solution: Mediator Pattern ---");
        ChatMediator chatroom = new ChatRoom();

        User u1 = new ChatUser(chatroom, "Charlie");
        User u2 = new ChatUser(chatroom, "Dave");
        User u3 = new ChatUser(chatroom, "Eve");

        chatroom.addUser(u1);
        chatroom.addUser(u2);
        chatroom.addUser(u3);

        // Charlie sends one message; Mediator handles the distribution
        u1.send("Group meeting at 5 PM.");
    }
}
