package lld.Structural_Design_Patterns;

import java.util.HashMap;
import java.util.Map;

/// ## Proxy Pattern
/// Provides a placeholder or surrogate for another object to control access to it.
///
/// **Examples:** Database connection pooling, Credit card (proxy for cash), Hibernate lazy loading.
///
/// ### The Problem: Direct Heavy Resource Access
/// Accessing heavy objects (like a remote downloader or large file) directly leads to:
/// - Unnecessary resource consumption (re-downloading same content).
/// - No security/access checks.
/// - Hard to add cross-cutting concerns like logging or caching.

// Legacy Downloader: No control, always downloads.
class RealVideoDownloader1 {
    public String downloadVideo1(String videoUrl) {
        System.out.println("Downloading video from URL: " + videoUrl);
        return "Video content from " + videoUrl;
    }
}

/// ### The Solution: Proxy Pattern
/// Use an interface to wrap the real object. The Proxy manages the lifecycle and access.
///
/// #### Understanding
/// - **Subject Interface (`VideoDownloader`):** Common interface for Real Subject and Proxy.
/// - **Real Subject (`RealVideoDownloader`):** The actual object that performs the work.
/// - **Proxy (`CachedVideoDownloader`):** Maintains a reference to the Real Subject, controls access, and manages caching.
///
/// #### Pros
/// - **Remote Proxy:** Manage communication with remote resources.
/// - **Virtual Proxy:** Lazy initialization of heavy objects.
/// - **Protection Proxy:** Add security/authentication checks.
/// - **Cache Proxy:** Store results of expensive operations.
///
/// #### Cons
/// - **Latency:** Introduces a small overhead for every call.
/// - **Complexity:** Adds more classes to the system.

// Subject Interface.
interface VideoDownloader {
    String downloadVideo(String videoURL);
}

/// Real Subject: Performs actual network download.
class RealVideoDownloader implements VideoDownloader {
    @Override
    public String downloadVideo(String videoUrl) {
        System.out.println("[Network] Downloading from: " + videoUrl);
        return "High Quality Video Data from " + videoUrl;
    }
}

/// Proxy Class: Implements Caching logic.
class CachedVideoDownloader implements VideoDownloader {
    private final RealVideoDownloader realDownloader;
    private final Map<String, String> cache;

    public CachedVideoDownloader() {
        this.realDownloader = new RealVideoDownloader();
        this.cache = new HashMap<>();
    }

    @Override
    public String downloadVideo(String videoUrl) {
        if (cache.containsKey(videoUrl)) {
            System.out.println("[Cache] Returning hit for: " + videoUrl);
            return cache.get(videoUrl);
        }

        System.out.println("[Proxy] Cache miss. Delegating to Real Downloader...");
        String video = realDownloader.downloadVideo(videoUrl);
        cache.put(videoUrl, video);
        return video;
    }
}

/// ## Summary of Proxy Pattern
///
/// ### Pros
/// - **Security:** Filter requests before they reach the real object.
/// - **Efficiency:** Save bandwidth/CPU using caching or lazy loading.
///
/// ### Cons
/// - **Indirection:** Code can be harder to follow due to multiple layers.
public class ProxyPattern {
    public static void main(String[] args) {
        // --- PROBLEM CASE ---
        System.out.println("--- Problem: Direct Access (No Caching) ---");
        RealVideoDownloader1 legacy = new RealVideoDownloader1();
        legacy.downloadVideo1("https://youtube.com/design-patterns");
        legacy.downloadVideo1("https://youtube.com/design-patterns");

        // --- SOLUTION CASE ---
        System.out.println("\n--- Solution: Proxy with Caching ---");
        VideoDownloader proxy = new CachedVideoDownloader();

        System.out.println("Request 1:");
        proxy.downloadVideo("https://youtube.com/design-patterns");

        System.out.println("\nRequest 2 (Duplicate):");
        proxy.downloadVideo("https://youtube.com/design-patterns");
    }
}
