package lld.Behavioral_Design_Patterns;

import java.util.*;

/// ## Memento Pattern
/// Behavioral pattern that lets you save and restore the previous state of an object
/// without revealing the details of its implementation.
///
/// **Examples:** Undo/Redo in text editors, Game save points, Browser history.
///
/// ### The Problem: Leaky Encapsulation
/// To implement "Undo", we often expose an object's internal fields to a "Snapshot" class.
/// This violates encapsulation and makes the snapshot dependent on the internal structure of the original object.

// Legacy Editor: Exposes fields to the snapshot class.
class LegacyResumeEditor {
    public String name;
    public List<String> skills = new ArrayList<>();

    // Client must manually copy fields
    public Map<String, Object> leakySave() {
        Map<String, Object> state = new HashMap<>();
        state.put("name", name);
        state.put("skills", new ArrayList<>(skills));
        return state;
    }
}

/// ### The Solution: Memento Pattern
/// Use a dedicated Memento object that is "opaque" to other objects but fully accessible to the Originator.
///
/// #### Understanding
/// - **Originator (`ResumeEditor`):** The object whose state needs to be saved. It creates the memento and uses it for restoration.
/// - **Memento (`Memento`):** A value object that acts as a snapshot. Usually an inner class of the Originator for private access.
/// - **Caretaker (`ResumeHistory`):** Manages the history (stack) of mementos but never modifies them.
///
/// #### Pros
/// - **Encapsulation:** Internal state remains hidden from the outside world.
/// - **Simplified Originator:** The task of managing history is moved to the Caretaker.
/// - **Integrity:** Mementos are typically immutable, preventing state corruption.
///
/// #### Cons
/// - **Memory Usage:** Storing many large snapshots can consume significant RAM.
/// - **Lifecycle:** Mementos might live longer than the Originator, complicating resource cleanup.

// ========== Originator ==========

// The object whose state we want to track.
class ResumeEditor {
    private String name;
    private List<String> skills = new ArrayList<>();

    public void setState(String name, List<String> skills) {
        this.name = name;
        this.skills = new ArrayList<>(skills);
    }

    /// Creates a snapshot of current state.
    public Memento save() {
        return new Memento(name, skills);
    }

    /// Restores state from a snapshot.
    public void undo(Memento memento) {
        this.name = memento.getName();
        this.skills = memento.getSkills();
    }

    public void display() {
        System.out.println("Editor -> Name: " + name + ", Skills: " + skills);
    }

    // ========== Inner Memento (Opaque to outside) ==========

    /// Encapsulates the state. Final fields ensure immutability.
    public static class Memento {
        private final String name;
        private final List<String> skills;

        private Memento(String name, List<String> skills) {
            this.name = name;
            this.skills = List.copyOf(skills);
        }

        private String getName() {
            return name;
        }

        private List<String> getSkills() {
            return skills;
        }
    }
}

// ========== Caretaker ==========

/// Manages the history of snapshots.
class ResumeHistory {
    private final Stack<ResumeEditor.Memento> history = new Stack<>();

    public void push(ResumeEditor.Memento m) {
        history.push(m);
    }

    public ResumeEditor.Memento pop() {
        return history.isEmpty() ? null : history.pop();
    }
}

/// ## Summary of Memento Pattern
///
/// ### Pros
/// - **State Isolation:** History management is decoupled from the business logic.
///
/// ### Cons
/// - **Overhead:** Frequent snapshots of large objects can degrade performance.
public class MementoPattern {
    public static void main(String[] args) {
        // --- PROBLEM CASE ---
        System.out.println("--- Problem: Leaky Encapsulation ---");
        LegacyResumeEditor legacy = new LegacyResumeEditor();
        legacy.name = "Alice";
        Map<String, Object> state = legacy.leakySave();
        System.out.println("State saved in a Map: " + state);

        // --- SOLUTION CASE ---
        System.out.println("\n--- Solution: Memento Pattern ---");
        ResumeEditor editor = new ResumeEditor();
        ResumeHistory history = new ResumeHistory();

        // 1. Initial State
        editor.setState("Alice", Arrays.asList("Java", "SQL"));
        history.push(editor.save());
        editor.display();

        // 2. Change State
        System.out.println("Updating resume...");
        editor.setState("Alice Johnson", Arrays.asList("Java", "SQL", "Spring"));
        editor.display();

        // 3. Undo
        System.out.println("Performing Undo...");
        ResumeEditor.Memento previous = history.pop();
        if (previous != null) {
            editor.undo(previous);
        }
        editor.display();
    }
}
