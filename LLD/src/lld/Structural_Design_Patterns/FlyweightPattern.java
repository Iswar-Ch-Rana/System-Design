package lld.Structural_Design_Patterns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/// ## Flyweight Pattern
/// Reduces memory usage by sharing as much data as possible with similar objects.
///
/// **Examples:** Document editors (sharing glyph styles), Game particles (bullets, trees), Cache.
///
/// ### The Problem: Memory Exhaustion
/// Creating millions of objects with identical data (e.g., name, color, texture)
/// consumes massive RAM. Most of the state is redundant.

// Legacy Tree: Stores all data in every instance (High RAM usage).
class LegacyTree {
    private final int x;
    private final int y; // Extrinsic (Unique)
    private final String name;
    private final String color;
    private final String texture; // Intrinsic (Redundant)

    public LegacyTree(int x, int y, String name, String color, String texture) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.color = color;
        this.texture = texture;
    }
}

/// ### The Solution: Flyweight Pattern
/// Separate object state into Intrinsic (Shared) and Extrinsic (Unique).
///
/// #### Understanding
/// - **Intrinsic State (`TreeType`):** Constant data shared across many objects.
/// - **Extrinsic State (`Tree`):** Unique data passed to the flyweight at runtime.
/// - **Flyweight Factory (`TreeFactory`):** Manages creation and caching of intrinsic objects.
///
/// #### Pros
/// - **RAM Efficiency:** Drastically reduces memory footprint for massive object counts.
/// - **Centralization:** Shared data is managed in one place.
///
/// #### Cons
/// - **Complexity:** Dividing state can make code harder to maintain.
/// - **CPU vs RAM:** Might trade some CPU (lookups) for RAM savings.
///
// ============= Intrinsic State (Flyweight) ================

// Shared data object.
class TreeType {
    private final String name;
    private final String color;
    private final String texture;

    public TreeType(String name, String color, String texture) {
        this.name = name;
        this.color = color;
        this.texture = texture;
    }

    /// Draws a tree using intrinsic data and extrinsic coordinates.
    public void draw(int x, int y) {
        System.out.println("Drawing " + name + " tree at (" + x + ", " + y + ")");
    }
}

// ============ Flyweight Factory ==============

/// Ensures that `TreeType` objects are reused instead of re-created.
class TreeFactory {
    private static final Map<String, TreeType> treeTypes = new HashMap<>();

    public static TreeType getTreeType(String name, String color, String texture) {
        String key = name + "_" + color + "_" + texture;
        if (!treeTypes.containsKey(key)) {
            treeTypes.put(key, new TreeType(name, color, texture));
        }
        return treeTypes.get(key);
    }
}

// ================ Extrinsic Context =================

/// Stores unique state and a reference to the Flyweight.
class Tree {
    private final int x;
    private final int y;
    private final TreeType type;

    public Tree(int x, int y, TreeType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public void draw() {
        type.draw(x, y);
    }
}

// ================ Client Class =================

class Forest {
    private final List<Tree> trees = new ArrayList<>();

    public void plantTree(int x, int y, String name, String color, String texture) {
        // Only unique data (x, y) + shared type reference stored in 'Tree'
        TreeType type = TreeFactory.getTreeType(name, color, texture);
        trees.add(new Tree(x, y, type));
    }

    public void draw() {
        for (Tree tree : trees) {
            tree.draw();
        }
    }
}

/// ## Summary of Flyweight Pattern
///
/// ### Pros
/// - **Scalability:** Enables handling of millions of objects that would otherwise crash the JVM.
///
/// ### Cons
/// - **Thread Safety:** Flyweight objects must be immutable to be safely shared across threads.
public class FlyweightPattern {
    public static void main(String[] args) {
        Forest forest = new Forest();

        // Plant 1 million trees using only 1 TreeType object
        for (int i = 0; i < 1000000; i++) {
            forest.plantTree(i, i, "Oak", "Green", "Rough");
        }

        System.out.println("Successfully planted 1 million trees.");
        System.out.println("Memory used by TreeType: 1 object (Shared).");
        System.out.println("Memory used by Trees: 1 million objects (Unique coordinates).");
    }
}
