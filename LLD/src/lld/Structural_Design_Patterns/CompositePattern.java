package lld.Structural_Design_Patterns;

import java.util.ArrayList;
import java.util.List;

/// ## Composite Pattern
/// Structural pattern that lets you compose objects into tree structures
/// and then work with these structures as if they were individual objects.
///
/// **Examples:** File system (Files/Folders), UI components, XML/JSON parsing.
///
/// ### The Problem: Manual Hierarchy Management
/// Client must distinguish between individual objects and collections.
/// Leads to complex `if-else` or `instanceof` checks when traversing the structure.

// Legacy Product (Leaf-only).
class LegacyProduct {
    private final String name;
    private final double price;

    public LegacyProduct(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public void display(String indent) {
        System.out.println(indent + "Legacy Product: " + name + " - ₹" + price);
    }
}

/// Legacy Bundle (Collection of Products).
class LegacyBundle {
    private final String name;
    private final List<LegacyProduct> products = new ArrayList<>();

    public LegacyBundle(String name) {
        this.name = name;
    }

    public void addProduct(LegacyProduct p) {
        products.add(p);
    }

    public double getTotalPrice() {
        return products.stream().mapToDouble(LegacyProduct::getPrice).sum();
    }
}

/// ### The Solution: Composite Pattern
/// Create a common interface for both leaves and composites.
///
/// #### Understanding
/// - **Component:** The interface for all objects in the composition.
/// - **Leaf:** Simple element with no children.
/// - **Composite:** Container that stores leaves or other composites.
///
/// #### Pros
/// - **Uniformity:** Client treats simple and complex elements the same way.
/// - **Recursion:** Easy to work with deeply nested tree structures.
/// - **Extensibility:** Add new component types without breaking client code.
///
/// #### Cons
/// - **Generalization:** Hard to restrict what can be added to a composite at compile time.

// Component Interface.
interface CartItem {
    double getPrice();

    void display(String indent);
}

/// Leaf: Simple product.
class Product implements CartItem {
    private final String name;
    private final double price;

    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public void display(String indent) {
        System.out.println(indent + "Product: " + name + " - ₹" + price);
    }
}

/// Composite: Bundle containing other `CartItem` objects.
class ProductBundle implements CartItem {
    private final String bundleName;
    private final List<CartItem> items = new ArrayList<>();

    public ProductBundle(String bundleName) {
        this.bundleName = bundleName;
    }

    public void addItem(CartItem item) {
        items.add(item);
    }

    @Override
    public double getPrice() {
        return items.stream().mapToDouble(CartItem::getPrice).sum();
    }

    @Override
    public void display(String indent) {
        System.out.println(indent + "Bundle: " + bundleName);
        for (CartItem item : items) {
            item.display(indent + "  ");
        }
    }
}

/// ## Summary of Composite Pattern
///
/// ### Pros
/// - **Clean Client Code:** No `instanceof` checks required for traversal.
///
/// ### Cons
/// - **Design Complexity:** Requires careful interface design to satisfy both leaf and composite needs.
public class CompositePattern {
    public static void main(String[] args) {
        // --- PROBLEM CASE ---
        System.out.println("--- Problem Case: Manual Management ---");
        LegacyProduct book = new LegacyProduct("Clean Code", 600);
        LegacyBundle bundle = new LegacyBundle("Coding Kit");
        bundle.addProduct(new LegacyProduct("Mouse", 800));

        // Client must handle different types separately
        List<Object> legacyCart = new ArrayList<>();
        legacyCart.add(book);
        legacyCart.add(bundle);

        for (Object item : legacyCart) {
            if (item instanceof LegacyProduct) {
                ((LegacyProduct) item).display("");
            } else if (item instanceof LegacyBundle) {
                System.out.println("Bundle detected: " + ((LegacyBundle) item).getTotalPrice());
            }
        }

        // --- SOLUTION CASE ---
        System.out.println("\n--- Solution Case: Composite Pattern ---");
        CartItem phone = new Product("iPhone 15", 79000);

        ProductBundle techCombo = new ProductBundle("Tech Combo");
        techCombo.addItem(new Product("AirPods", 19000));
        techCombo.addItem(new Product("Charger", 2000));

        ProductBundle megaBundle = new ProductBundle("Mega Store Sale");
        megaBundle.addItem(phone);
        megaBundle.addItem(techCombo); // Composite inside Composite

        // Uniform treatment
        megaBundle.display("");
        System.out.println("Total Price: ₹" + megaBundle.getPrice());
    }
}
