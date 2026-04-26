package lld.Behavioral_Design_Patterns;

import java.util.Arrays;
import java.util.List;

/// ## Visitor Pattern
/// Behavioral pattern that lets you separate algorithms from the objects on which they operate.
///
/// **Examples:** Tax calculation for different product types, Compiler syntax tree traversal, Document export (to PDF, HTML).
///
/// ### The Problem: Operations Pollution
/// Adding new operations (like `calculateTax` or `exportToJson`) to a class hierarchy
/// requires modifying every class in that hierarchy.
/// Client code often ends up with messy `if (item instanceof PhysicalProduct)` blocks.

// Legacy Product: Methods for specific operations are mixed with data.
class LegacyPhysicalProduct {
    void printInvoice() {
        System.out.println("Legacy: Physical Invoice");
    }

    double calcShipping() {
        return 50.0;
    }
}

/// ### The Solution: Visitor Pattern
/// Move the operations to separate classes (Visitors). The elements only provide an `accept()` method.
///
/// #### Understanding
/// - **Element Interface (`Item`):** Declares an `accept()` method that takes a visitor.
/// - **Concrete Elements (`PhysicalProduct`, `DigitalProduct`):** Call the visitor's method corresponding to their class.
/// - **Visitor Interface (`ItemVisitor`):** Declares visit methods for each element type.
/// - **Concrete Visitor (`InvoiceVisitor`, `ShippingVisitor`):** Implements different versions of the same algorithm.
///
/// #### Pros
/// - **Open/Closed Principle:** Add new operations by creating new visitors without touching elements.
/// - **Single Responsibility:** Group related behaviors into a single visitor class.
/// - **Double Dispatch:** Elements choose the right operation based on their own type.
///
/// #### Cons
/// - **Rigidity:** Adding a new element type requires updating the Visitor interface and all its implementations.

// ========== Element Hierarchy ==========

// Element Interface.
interface Item {
    void accept(ItemVisitor visitor);
}

class PhysicalProduct implements Item {
    String name;
    double weight;

    public PhysicalProduct(String name, double weight) {
        this.name = name;
        this.weight = weight;
    }

    @Override
    public void accept(ItemVisitor visitor) {
        // Double Dispatch: The element knows its own type
        visitor.visit(this);
    }
}

class DigitalProduct implements Item {
    String name;

    public DigitalProduct(String name) {
        this.name = name;
    }

    @Override
    public void accept(ItemVisitor visitor) {
        visitor.visit(this);
    }
}

// ========== Visitor Interface ==========

/// Interface for all algorithms that operate on `Item` objects.
interface ItemVisitor {
    void visit(PhysicalProduct item);

    void visit(DigitalProduct item);
}

// ========== Concrete Visitors ==========

/// Operation 1: Generate Invoices.
class InvoiceVisitor implements ItemVisitor {
    @Override
    public void visit(PhysicalProduct item) {
        System.out.println("Invoice -> " + item.name + " (Physical): Logistics initiated.");
    }

    @Override
    public void visit(DigitalProduct item) {
        System.out.println("Invoice -> " + item.name + " (Digital): Download link generated.");
    }
}

/// Operation 2: Calculate Shipping.
class ShippingVisitor implements ItemVisitor {
    @Override
    public void visit(PhysicalProduct item) {
        double cost = item.weight * 15;
        System.out.println("Shipping -> " + item.name + ": ₹" + cost);
    }

    @Override
    public void visit(DigitalProduct item) {
        System.out.println("Shipping -> " + item.name + ": Free (Digital Delivery).");
    }
}

/// ## Summary of Visitor Pattern
///
/// ### Pros
/// - **Data/Logic Separation:** Keeps element classes focused on data storage.
///
/// ### Cons
/// - **Access:** Visitors might need access to private fields of elements, violating encapsulation.
public class VisitorPattern {
    public static void main(String[] args) {
        // --- PROBLEM CASE ---
        System.out.println("--- Problem: instanceof Checks ---");
        Object legacy = new LegacyPhysicalProduct();
        if (legacy instanceof LegacyPhysicalProduct) {
            ((LegacyPhysicalProduct) legacy).printInvoice();
        }

        // --- SOLUTION CASE ---
        System.out.println("\n--- Solution: Visitor Pattern ---");
        List<Item> cart = Arrays.asList(
                new PhysicalProduct("Running Shoes", 1.2),
                new DigitalProduct("Java LLD Course")
        );

        ItemVisitor invoiceGen = new InvoiceVisitor();
        ItemVisitor shipCalc = new ShippingVisitor();

        // One traversal, multiple operations via different visitors
        for (Item item : cart) {
            item.accept(invoiceGen);
            item.accept(shipCalc);
            System.out.println();
        }
    }
}
