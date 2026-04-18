package lld.Creational_Design_Patterns;

/// ## Factory Pattern
/// The Factory Pattern provides an interface for creating objects in a superclass, 
/// but allows subclasses to alter the type of objects that will be created.
///
/// **Examples:** Document readers (PDF vs Word), UI Elements (Button for Mac vs Windows).

/// ### Core Components
///
/// #### 1. Product Interface (`Logistics`)
/// Defines the common operations for all concrete products.
interface Logistics {
    /// Sends the shipment using the specific transport logic.
    void send();
}

/// #### 2. Concrete Products (`Road`, `Air`)
/// Specific implementations of the `Logistics` interface.
class Road implements Logistics {
    @Override
    public void send() {
        System.out.println("Sending by road logic");
    }
}

class Air implements Logistics {
    @Override
    public void send() {
        System.out.println("Sending by air logic");
    }
}

/// #### 3. Factory Class (`LogisticsFactory`)
///
/// ##### Understanding
/// - Centralizes object creation logic.
/// - Client doesn't need to know which concrete class is being instantiated.
///
/// ##### Pros
/// - **Loose Coupling:** Client code is independent of concrete implementation.
/// - **Single Responsibility:** Creation logic is moved to one place.
///
/// ##### Cons
/// - **Complexity:** Can lead to many small classes.
class LogisticsFactory {
    /// Creates a Logistics instance based on the provided mode.
    ///
    /// @param mode The mode of transport (e.g., "Air", "Road").
    /// @return A concrete implementation of the `Logistics` interface.
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
    public void send(String mode) {
        Logistics logistics = LogisticsFactory.getLogistics(mode);
        logistics.send();
    }
}

/// ## Summary of Factory Pattern
///
/// ### Pros
/// - **Scalability:** Easy to add new product types without changing client code.
/// - **Clean Code:** Avoids complex `if-else` or `switch` blocks in business logic.
///
/// ### Cons
/// - **Boilerplate:** Requires creating an interface and multiple subclasses.
public class FactoryPattern {
    public static void main(String[] args) {
        try {
            LogisticsService service = new LogisticsService();
            service.send("Air");
            service.send("Road");
            service.send("mall");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
