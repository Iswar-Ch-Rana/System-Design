package lld.Behavioral_Design_Patterns;

import java.util.Stack;

/// ## Command Pattern
/// Behavioral pattern that turns a request into a stand-alone object
/// that contains all information about the request.
///
/// **Examples:** Remote controls, Transactional systems, GUI buttons, Undo/Redo stacks.
///
/// ### The Problem: Hardcoded Invocation
/// The Invoker (Remote) is tightly coupled to specific Receiver (Light/AC) methods.
/// Adding a new device requires modifying the Invoker. Undo logic becomes complex with `switch` statements.

// Legacy Remote: Tightly coupled to Light and AC.
class LegacyRemoteControl {
    private final Light light;
    private String lastAction = "";

    public LegacyRemoteControl(Light light) {
        this.light = light;
    }

    public void pressLightOn() {
        light.on();
        lastAction = "ON";
    }

    /// Undo logic is hardcoded and hard to scale.
    public void pressUndo() {
        if (lastAction.equals("ON")) light.off();
    }
}

/// ### The Solution: Command Pattern
/// Encapsulate the request as an object. The Invoker only knows how to trigger the command.
///
/// #### Understanding
/// - **Command Interface (`Command`):** Interface for executing and undoing an operation.
/// - **Concrete Command (`LightOnCommand`):** Defines binding between Receiver and action.
/// - **Receiver (`Light`, `AC`):** The object that knows how to perform the work.
/// - **Invoker (`RemoteControl`):** Triggers the command. Does not know how the work is done.
///
/// #### Pros
/// - **Decoupling:** Invoker and Receiver are independent.
/// - **Undo/Redo:** Commands can store state to reverse actions.
/// - **Composite Commands:** Can group multiple commands into a "Macro".
///
/// #### Cons
/// - **Boilerplate:** Requires many small classes for every action.

// ========== Receiver Classes ==========

class Light {
    public void on() {
        System.out.println("Light ON");
    }

    public void off() {
        System.out.println("Light OFF");
    }
}

class AC {
    public void on() {
        System.out.println("AC ON");
    }

    public void off() {
        System.out.println("AC OFF");
    }
}

// ========== Command Interface ==========

interface Command {
    void execute();

    void undo();
}

// ========== Concrete Commands ==========

class LightOnCommand implements Command {
    private final Light light;

    public LightOnCommand(Light light) {
        this.light = light;
    }

    @Override
    public void execute() {
        light.on();
    }

    @Override
    public void undo() {
        light.off();
    }
}

class LightOffCommand implements Command {
    private final Light light;

    public LightOffCommand(Light light) {
        this.light = light;
    }

    @Override
    public void execute() {
        light.off();
    }

    @Override
    public void undo() {
        light.on();
    }
}

class AConCommand implements Command {
    private final AC ac;

    public AConCommand(AC ac) {
        this.ac = ac;
    }

    @Override
    public void execute() {
        ac.on();
    }

    @Override
    public void undo() {
        ac.off();
    }
}

// ========== Invoker Class ==========

class RemoteControl {
    private final Stack<Command> history = new Stack<>();

    public void executeCommand(Command command) {
        command.execute();
        history.push(command);
    }

    public void pressUndo() {
        if (!history.isEmpty()) {
            System.out.print("[Undo] ");
            history.pop().undo();
        } else {
            System.out.println("Nothing to undo.");
        }
    }
}

/// ## Summary of Command Pattern
///
/// ### Pros
/// - **Extensibility:** Add new commands without changing Invoker or Receiver.
///
/// ### Cons
/// - **Indirection:** Adds layers between the trigger and the action.
public class CommandPattern {
    public static void main(String[] args) {
        // --- PROBLEM CASE ---
        System.out.println("--- Problem: Hardcoded Remote ---");
        Light light = new Light();
        LegacyRemoteControl legacy = new LegacyRemoteControl(light);
        legacy.pressLightOn();
        legacy.pressUndo();

        // --- SOLUTION CASE ---
        System.out.println("\n--- Solution: Command Pattern ---");
        AC ac = new AC();
        RemoteControl remote = new RemoteControl();

        // Commands as objects
        Command lightOn = new LightOnCommand(light);
        Command acOn = new AConCommand(ac);

        // Execute and Undo work uniformly
        remote.executeCommand(lightOn);
        remote.executeCommand(acOn);

        remote.pressUndo(); // AC Off
        remote.pressUndo(); // Light Off
    }
}
