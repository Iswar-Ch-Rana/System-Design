package lld.Creational_Design_Patterns;

/// ## Factory Pattern
/// The Factory Pattern is a creational design pattern that provides an interface for creating objects in a superclass, 
/// but allows subclasses to alter the type of objects that will be created.
///
/// ### Core Components
/// - **Product Interface:** Defines the interface of objects the factory method creates.
/// - **Concrete Products:** Implementations of the product interface.
/// - **Factory Class:** Contains the logic to instantiate the appropriate concrete product based on input.

interface Logistics {
    void send();
}

/// Concrete Product: Road Logistics implementation.
class Road implements Logistics {
    @Override
    public void send() {
        System.out.println("Sending by road logic");
    }
}

/// Concrete Product: Air Logistics implementation.
class Air implements Logistics {
    @Override
    public void send() {
        System.out.println("Sending by air logic");
    }
}

/// Factory Class responsible for object creation.
///
/// Decouples the client code from the concrete classes being instantiated.
class LogisticsFactory {
    /// Creates a Logistics instance based on the provided mode.
    ///
    /// @param mode The mode of transport (e.g., "Air", "Road").
    /// @return A concrete implementation of the `Logistics` interface.
    /// @throws IllegalArgumentException if the mode is not recognized.
    public static Logistics getLogistics(String mode) {
        if (mode.equalsIgnoreCase("Air")) {
            return new Air();
        } else if (mode.equalsIgnoreCase("Road")) {
            return new Road();
        }
        throw new IllegalArgumentException("Unknown logistics mode: " + mode);
    }
}

/// Service class that uses the Factory to perform operations.
class LogisticsService {
    /// Initiates a shipment by requesting the appropriate object from the Factory.
    ///
    /// @param mode The transport mode requested by the client.
    public void send(String mode) {
        // Using the Logistics Factory to get the desired object based on the mode
        Logistics logistics = LogisticsFactory.getLogistics(mode);
        logistics.send();
    }
}

/// ### Factory Pattern Execution
/// Demonstrates how the `LogisticsService` can interact with different logistics types
/// without knowing the concrete implementation details.
public class FactoryPattern {
    public static void main(String[] args) {
        try {
            LogisticsService service = new LogisticsService();
            service.send("Air");
            service.send("Road");
            
            // This will trigger the exception handling
            service.send("mall");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
