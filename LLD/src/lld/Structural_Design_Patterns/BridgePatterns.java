package lld.Structural_Design_Patterns;

/// ## Bridge Pattern
/// Decouples an abstraction from its implementation so that the two can vary independently.
///
/// **Examples:** Remote control (Abstraction) vs Device (Implementation), GUI Framework vs Operating System.
///
/// ### The Problem: Class Explosion (Cartesian Product)
/// If we have 3 platforms (Web, Mobile, TV) and 3 qualities (HD, 4K, 8K),
/// a naive inheritance approach requires 3x3 = 9 classes.
/// Adding one more platform requires 3 new classes.

// Legacy Approach: Combination classes.
class WebHDPlayer {
    void play() {
        System.out.println("Web HD");
    }
}

class MobileHDPlayer {
    void play() {
        System.out.println("Mobile HD");
    }
}

class Web4KPlayer {
    void play() {
        System.out.println("Web 4K");
    }
}
// ... and so on ...

/// ### The Solution: Bridge Pattern
/// Separate the "Platform" (Abstraction) from the "Quality" (Implementation).
///
/// #### Understanding
/// - **Abstraction (`VideoPlayer`):** High-level control logic. Holds a reference to the Implementor.
/// - **Refined Abstraction (`WebPlayer`, `MobilePlayer`):** Specific platform variants.
/// - **Implementor (`VideoProcessor`):** Low-level platform-independent interface.
/// - **Concrete Implementor (`HDProcessor`, `UltraHDProcessor`):** Specific quality logic.
///
/// #### Pros
/// - **Platform Independence:** Add new platforms without changing quality logic.
/// - **Implementation Independence:** Add new qualities without changing platform logic.
/// - **Open/Closed Principle:** Extend either dimension independently.
///
/// #### Cons
/// - **Complexity:** Increases code complexity by introducing new interfaces and classes.
///
// ========== Implementation (The "Bridge" Side) ==========

// Implementor Interface.
interface VideoProcessor {
    void process(String title);
}

/// Concrete Implementor for HD.
class HDProcessor implements VideoProcessor {
    @Override
    public void process(String title) {
        System.out.println("Processing " + title + " in 1080p HD.");
    }
}

/// Concrete Implementor for 4K.
class UltraHDProcessor implements VideoProcessor {
    @Override
    public void process(String title) {
        System.out.println("Processing " + title + " in 4K Ultra HD.");
    }
}

// ========== Abstraction (The "Client" Side) ==========

/// Abstraction base class.
abstract class VideoPlayer {
    // The Bridge: Reference to the implementation
    protected VideoProcessor processor;

    protected VideoPlayer(VideoProcessor processor) {
        this.processor = processor;
    }

    abstract void play(String title);
}

/// Refined Abstraction for Web.
class WebPlayer extends VideoPlayer {
    public WebPlayer(VideoProcessor processor) {
        super(processor);
    }

    @Override
    void play(String title) {
        System.out.print("[Web Player] ");
        processor.process(title);
    }
}

/// Refined Abstraction for Mobile.
class MobilePlayer extends VideoPlayer {
    public MobilePlayer(VideoProcessor processor) {
        super(processor);
    }

    @Override
    void play(String title) {
        System.out.print("[Mobile Player] ");
        processor.process(title);
    }
}

/// ## Summary of Bridge Pattern
///
/// ### Pros
/// - **Scalability:** `M` platforms + `N` qualities = `M + N` classes (instead of `M * N`).
///
/// ### Cons
/// - **Double Dispatch:** Slightly more complex call stack.
public class BridgePatterns {
    public static void main(String[] args) {
        // --- PROBLEM CASE ---
        System.out.println("--- Problem: Combination Classes ---");
        WebHDPlayer legacyWebHD = new WebHDPlayer();
        legacyWebHD.play();

        // --- SOLUTION CASE ---
        System.out.println("\n--- Solution: Bridge Pattern ---");

        // Web playing HD
        VideoPlayer player1 = new WebPlayer(new HDProcessor());
        player1.play("Interstellar");

        // Web playing 4K
        VideoPlayer player2 = new WebPlayer(new UltraHDProcessor());
        player2.play("Interstellar");

        // Mobile playing 4K
        VideoPlayer player3 = new MobilePlayer(new UltraHDProcessor());
        player3.play("The Dark Knight");

        // We can easily add a 'DesktopPlayer' or '8KProcessor' without affecting others.
    }
}
