package lld.Behavioral_Design_Patterns;

import java.util.ArrayList;
import java.util.List;

/// ## Iterator Pattern
/// Behavioral pattern that lets you traverse elements of a collection
/// without exposing its underlying representation (list, stack, tree, etc.).
///
/// **Examples:** Java `Iterator`, Database cursors, File system scanners.
///
/// ### The Problem: Exposed Internal Structure
/// Client code becomes dependent on the specific collection type (e.g., `ArrayList`).
/// If the data structure changes (e.g., to a `LinkedList` or `Set`), the client code breaks.

// Legacy Playlist: Exposes internal `List`.
class LegacyYouTubePlaylist {
    private final List<Video> videos = new ArrayList<>();

    public void addVideo(Video video) {
        videos.add(video);
    }

    /// Violates encapsulation by exposing internal data structure.
    public List<Video> getVideos() {
        return videos;
    }
}

/// ### The Solution: Iterator Pattern
/// Encapsulate traversal logic in a separate object called an Iterator.
///
/// #### Understanding
/// - **Aggregate Interface (`Playlist`):** Interface for the collection.
/// - **Concrete Aggregate (`YouTubePlaylist`):** Implementation that returns an iterator.
/// - **Iterator Interface (`PlaylistIterator`):** Defines methods for traversal (`next()`, `hasNext()`).
/// - **Concrete Iterator (`YouTubePlaylistIterator`):** Tracks current position and knows how to traverse the aggregate.
///
/// #### Pros
/// - **Encapsulation:** Underlying collection remains hidden.
/// - **Clean Client Code:** Traversal logic is separated from business logic.
/// - **Multiple Traversals:** Can have multiple iterators active on the same collection.
///
/// #### Cons
/// - **Overkill:** Not needed for simple applications with basic collections.
/// - **Performance:** Slightly more overhead than a simple `for` loop on a raw list.

// Data object.
record Video(String title) {
}

// ========== Iterator Interfaces ==========

/// Traversal contract.
interface PlaylistIterator {
    boolean hasNext();

    Video next();
}

/// Collection contract.
interface Playlist {
    PlaylistIterator createIterator();
}

// ========== Concrete Implementation ==========

/// Concrete Aggregate.
class YouTubePlaylist implements Playlist {
    private final List<Video> videos = new ArrayList<>();

    public void addVideo(Video video) {
        videos.add(video);
    }

    @Override
    public PlaylistIterator createIterator() {
        return new YouTubePlaylistIterator(videos);
    }
}

/// Concrete Iterator.
class YouTubePlaylistIterator implements PlaylistIterator {
    private final List<Video> videos;
    private int position = 0;

    public YouTubePlaylistIterator(List<Video> videos) {
        this.videos = videos;
    }

    @Override
    public boolean hasNext() {
        return position < videos.size();
    }

    @Override
    public Video next() {
        return hasNext() ? videos.get(position++) : null;
    }
}

/// ## Summary of Iterator Pattern
///
/// ### Pros
/// - **Single Responsibility:** Traversal logic is isolated.
/// - **Uniformity:** Can traverse different collections using the same client code.
///
/// ### Cons
/// - **Complexity:** Adds more interfaces and classes to the project.
public class IteratorPattern {
    public static void main(String[] args) {
        // --- PROBLEM CASE ---
        System.out.println("--- Problem: Exposed Collection ---");
        LegacyYouTubePlaylist legacy = new LegacyYouTubePlaylist();
        legacy.addVideo(new Video("LLD Part 1"));

        // Client must know it's a List to iterate
        for (Video v : legacy.getVideos()) {
            System.out.println("Traversing legacy: " + v.title());
        }

        // --- SOLUTION CASE ---
        System.out.println("\n--- Solution: Iterator Pattern ---");
        YouTubePlaylist playlist = new YouTubePlaylist();
        playlist.addVideo(new Video("Design Patterns 101"));
        playlist.addVideo(new Video("Advanced LLD"));

        // Client asks for iterator, doesn't care about the internal List
        PlaylistIterator iterator = playlist.createIterator();

        while (iterator.hasNext()) {
            System.out.println("Traversing with Iterator: " + iterator.next().title());
        }
    }
}
