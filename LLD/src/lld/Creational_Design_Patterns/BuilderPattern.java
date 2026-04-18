package lld.Creational_Design_Patterns;

import java.util.Arrays;
import java.util.List;

/// ## Builder Pattern
/// Creational pattern for constructing complex objects step by step.
///
/// **Examples:** Building a PC (CPU, RAM, GPU), Fast-food order customization.
///
/// ### Understanding
/// - **Step-by-step Construction:** Objects are built incrementally.
/// - **Encapsulation:** Object construction is separated from its representation.
/// - **Immutability:** Resulting object can be final and thread-safe.
///
/// ### Pros
/// - **Avoids Telescoping Constructor:** Readable code even with many fields.
/// - **Optional Parameters:** No need for complex overloading.
/// - **Immutable Objects:** Can be created safely with private constructors.
///
/// ### Cons
/// - **Verbosity:** Requires many small methods for each property.
/// - **Duplicate Code:** Builder often mimics the original class's fields.

// Represents a customizable Burger Meal.
class BurgerMeal {
    private final String bunType;
    private final String patty;

    private final boolean hasCheese;
    private final List<String> toppings;
    private final String side;
    private final String drink;

    /// Private constructor to force use of Builder.
    private BurgerMeal(BurgerBuilder builder) {
        this.bunType = builder.bunType;
        this.patty = builder.patty;
        this.hasCheese = builder.hasCheese;
        this.toppings = builder.toppings;
        this.side = builder.side;
        this.drink = builder.drink;
    }

    /// Static nested Builder class.
    public static class BurgerBuilder {
        private final String bunType;
        private final String patty;

        private boolean hasCheese;
        private List<String> toppings;
        private String side;
        private String drink;

        /// Builder constructor with required fields.
        public BurgerBuilder(String bunType, String patty) {
            this.bunType = bunType;
            this.patty = patty;
        }

        public BurgerBuilder withCheese(boolean hasCheese) {
            this.hasCheese = hasCheese;
            return this;
        }

        public BurgerBuilder withToppings(List<String> toppings) {
            this.toppings = toppings;
            return this;
        }

        public BurgerBuilder withSide(String side) {
            this.side = side;
            return this;
        }

        public BurgerBuilder withDrink(String drink) {
            this.drink = drink;
            return this;
        }

        /// Final build method.
        ///
        /// @return New `BurgerMeal` instance.
        public BurgerMeal build() {
            return new BurgerMeal(this);
        }
    }
}

/// ## Summary of Builder Pattern
///
/// ### Pros
/// - **Fluent API:** Clean method chaining for configuration.
/// - **Valid Construction:** Ensures mandatory fields are set at once.
///
/// ### Cons
/// - **Complexity:** Excessive boilerplate for simple objects.
public class BuilderPattern {
    public static void main(String[] args) {
        // Plain
        BurgerMeal plainBurger = new BurgerMeal.BurgerBuilder("wheat", "veg")
                .build();

        // Loaded
        List<String> toppings = Arrays.asList("lettuce", "onion", "jalapeno");
        BurgerMeal loadedBurger = new BurgerMeal.BurgerBuilder("multigrain", "chicken")
                .withCheese(true)
                .withToppings(toppings)
                .withSide("fries")
                .withDrink("coke")
                .build();
    }
}
