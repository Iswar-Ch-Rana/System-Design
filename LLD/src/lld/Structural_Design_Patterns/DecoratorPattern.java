package lld.Structural_Design_Patterns;

/// ## Decorator Pattern
/// Attaches new behaviors to objects dynamically by placing them inside wrapper objects.
///
/// **Examples:** UI scrollbars, File streams (Buffered, Compressed), Coffee toppings.
///
/// ### The Problem: Class Explosion
/// Without Decorators, every combination of features requires a unique subclass.
/// This leads to an unmanageable hierarchy.

// Base pizza class (Inheritance approach - leads to explosion).
class PlainPizzas {
}

class CheesePizzas extends PlainPizzas {
}

class OlivePizzas extends PlainPizzas {
}

class StuffedPizzas extends PlainPizzas {
}

class CheeseStuffedPizzas extends CheesePizzas {
}

class CheeseOlivePizzas extends CheesePizzas {
}

class CheeseOliveStuffedPizzas extends CheeseOlivePizzas {
}

/// ### The Solution: Decorator Pattern
/// Wrap the base object with feature-specific decorators.
///
/// #### Understanding
/// - **Component:** The interface/class being decorated.
/// - **Concrete Component:** The basic object that can have features added.
/// - **Decorator:** Maintains a reference to a Component and implements its interface.
/// - **Concrete Decorator:** Adds specific state or behavior to the Component.
///
/// #### Pros
/// - **Flexible:** Add/Remove responsibilities at runtime.
/// - **Modular:** Combine behaviors by wrapping multiple decorators.
/// - **Clean Hierarchy:** Avoids hundreds of subclasses for every feature combination.
///
/// #### Cons
/// - **Complexity:** Many small objects can make debugging harder.
/// - **Order Dependency:** Sometimes the order of decoration matters.

// Component Interface for all Pizzas.
interface Pizza {
    String getDescription();

    double getCost();
}

// ============= Concrete Components: Base pizza ==============

/// Basic plain pizza implementation.
class PlainPizza implements Pizza {
    @Override
    public String getDescription() {
        return "Plain Pizza";
    }

    @Override
    public double getCost() {
        return 150.00;
    }
}

/// Margherita base pizza.
class MargheritaPizza implements Pizza {
    @Override
    public String getDescription() {
        return "Margherita Pizza";
    }

    @Override
    public double getCost() {
        return 200.00;
    }
}

// ======================== Abstract Decorator ===========================

/// Base Decorator: Implements `Pizza` and holds a reference to a `Pizza` object.
abstract class PizzaDecorator implements Pizza {
    protected Pizza pizza;

    public PizzaDecorator(Pizza pizza) {
        this.pizza = pizza;
    }
}

// ============ Concrete Decorators ================

/// Adds Extra Cheese behavior to a Pizza.
class ExtraCheese extends PizzaDecorator {
    public ExtraCheese(Pizza pizza) {
        super(pizza);
    }

    @Override
    public String getDescription() {
        return pizza.getDescription() + ", Extra Cheese";
    }

    @Override
    public double getCost() {
        return pizza.getCost() + 40.0;
    }
}

/// Adds Olives behavior to a Pizza.
class Olives extends PizzaDecorator {
    public Olives(Pizza pizza) {
        super(pizza);
    }

    @Override
    public String getDescription() {
        return pizza.getDescription() + ", Olives";
    }

    @Override
    public double getCost() {
        return pizza.getCost() + 30.0;
    }
}

/// Adds Stuffed Crust behavior to a Pizza.
class StuffedCrust extends PizzaDecorator {
    public StuffedCrust(Pizza pizza) {
        super(pizza);
    }

    @Override
    public String getDescription() {
        return pizza.getDescription() + ", Stuffed Crust";
    }

    @Override
    public double getCost() {
        return pizza.getCost() + 50.0;
    }
}

/// ## Summary of Decorator Pattern
///
/// ### Pros
/// - **Open/Closed Principle:** New features can be added without modifying existing code.
/// - **Dynamic Composition:** Features added at runtime instead of compile-time.
///
/// ### Cons
/// - **Identification:** Hard to tell which decorator is which in a long chain.
public class DecoratorPattern {
    public static void main(String[] args) {
        // Start with a basic Margherita Pizza
        Pizza myPizza = new MargheritaPizza();

        // Add toppings dynamically
        myPizza = new ExtraCheese(myPizza);
        myPizza = new Olives(myPizza);
        myPizza = new StuffedCrust(myPizza);

        System.out.println("Pizza Description: " + myPizza.getDescription());
        System.out.println("Total Cost: ₹" + myPizza.getCost());
    }
}
