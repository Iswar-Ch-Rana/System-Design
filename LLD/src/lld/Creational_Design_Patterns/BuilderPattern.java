package lld.Creational_Design_Patterns;

import java.util.Arrays;
import java.util.List;

/// ## Builder Pattern
/// Creational pattern for constructing complex objects step by step.
///
/// ### Why use?
/// - **Immutability:** Resulting object is immutable (final fields).
/// - **Readable:** Avoids "telescoping constructors" with many parameters.
/// - **Flexible:** Allows optional parameters without complex constructor overloading.

class BurgerMeal {
    private final String bunType;
    private final String patty;

    // Optional components
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
        // Required
        private final String bunType;
        private final String patty;

        // Optional
        private boolean hasCheese;
        private List<String> toppings;
        private String side;
        private String drink;

        /// Builder constructor with required fields.
        ///
        /// @param bunType Required bun type.
        /// @param patty   Required patty type.
        public BurgerBuilder(String bunType, String patty) {
            this.bunType = bunType;
            this.patty = patty;
        }

        /// Sets cheese preference.
        /// @return Current builder instance.
        public BurgerBuilder withCheese(boolean hasCheese) {
            this.hasCheese = hasCheese;
            return this;
        }

        /// Sets toppings list.
        /// @return Current builder instance.
        public BurgerBuilder withToppings(List<String> toppings) {
            this.toppings = toppings;
            return this;
        }

        /// Sets side dish.
        /// @return Current builder instance.
        public BurgerBuilder withSide(String side) {
            this.side = side;
            return this;
        }

        /// Sets drink.
        /// @return Current builder instance.
        public BurgerBuilder withDrink(String drink) {
            this.drink = drink;
            return this;
        }

        /// Final build method.
        /// @return New `BurgerMeal` instance.
        public BurgerMeal build() {
            return new BurgerMeal(this);
        }
    }
}

/// ### Builder Pattern Execution
/// Demonstrates fluent API for object creation.
public class BuilderPattern {
    public static void main(String[] args) {
        // Creating burger with only required fields
        BurgerMeal plainBurger = new BurgerMeal.BurgerBuilder("wheat", "veg")
                .build();

        // Burger with cheese only
        BurgerMeal burgerWithCheese = new BurgerMeal.BurgerBuilder("wheat", "veg")
                .withCheese(true)
                .build();

        // Fully loaded burger
        List<String> toppings = Arrays.asList("lettuce", "onion", "jalapeno");
        BurgerMeal loadedBurger = new BurgerMeal.BurgerBuilder("multigrain", "chicken")
                .withCheese(true)
                .withToppings(toppings)
                .withSide("fries")
                .withDrink("coke")
                .build();
    }
}
