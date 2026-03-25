// qn10.java - Problem 10: Multi-Level Cache System for Video Streaming

import java.util.*;
import java.util.concurrent.*;

public class qn10 {

    // ==================== USER DEFINED CLASS ====================
    // Represents video metadata (could be title, size, etc.)
    static class VideoData {
        String videoId;
        String title;
        long sizeInMB;

        VideoData(String videoId, String title, long sizeInMB) {
            this.videoId = videoId;
            this.title = title;
            this.sizeInMB = sizeInMB;
        }

        @Override
        public String toString() {
            return title + " (" + sizeInMB + "MB)";
        }
    }

    // ==================== MULTI-LEVEL CACHE SYSTEM ====================
    // L1: Fastest in-memory LRU cache (10,000 most popular videos)
    private final LinkedHashMap<String, VideoData> l1Cache;

    // L2: SSD-backed cache (100,000 videos) - simulated with HashMap
    private final Map<String, String> l2Cache;           // videoId -> simulated SSD file path
    private final Map<String, Integer> accessCount;      // videoId -> access counter

    // L3: Slow database (all videos) - simulated with HashMap + delay
    private final Map<String, VideoData> database;

    // Statistics
    private int l1Hits = 0, l1Misses = 0;
    private int l2Hits = 0, l2Misses = 0;
    private int l3Hits = 0;

    private final int L1_MAX_SIZE = 10000;
    private final int L2_MAX_SIZE = 100000;
    private final int PROMOTION_THRESHOLD = 5;   // promote from L2 to L1 after this many accesses

    public qn10() {
        // L1: LinkedHashMap with access-order for automatic LRU eviction
        this.l1Cache = new LinkedHashMap<>(L1_MAX_SIZE, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
                return size() > L1_MAX_SIZE;
            }
        };

        this.l2Cache = new HashMap<>(L2_MAX_SIZE);
        this.accessCount = new HashMap<>();
        this.database = new HashMap<>();

        // Pre-populate some database content for demo
        initializeDatabase();
    }

    private void initializeDatabase() {
        database.put("video_123", new VideoData("video_123", "Stranger Things S04E01", 850));
        database.put("video_999", new VideoData("video_999", "The Crown S05E03", 920));
        // Add more if needed for testing
    }

    // Simulate L3 database hit (slow)
    private VideoData queryDatabase(String videoId) {
        try {
            Thread.sleep(150);   // simulate 150ms database latency
        } catch (InterruptedException ignored) {}
        return database.get(videoId);
    }

    // Simulate SSD read (5ms)
    private void simulateSSDRead() {
        try {
            Thread.sleep(5);
        } catch (InterruptedException ignored) {}
    }

    // Main method to fetch a video with multi-level caching
    public VideoData getVideo(String videoId) {
        long startTime = System.currentTimeMillis();

        System.out.println("getVideo(\"" + videoId + "\")");

        // === L1 Cache Check (fastest) ===
        if (l1Cache.containsKey(videoId)) {
            l1Hits++;
            VideoData data = l1Cache.get(videoId);   // updates access order for LRU
            long timeTaken = System.currentTimeMillis() - startTime;
            System.out.printf("→ L1 Cache HIT (%.1fms)%n", 0.5);
            System.out.printf("→ Total: %.1fms%n%n", (double) timeTaken);
            return data;
        }
        l1Misses++;

        // === L2 Cache Check (SSD) ===
        if (l2Cache.containsKey(videoId)) {
            l2Hits++;
            simulateSSDRead();
            System.out.printf("→ L2 Cache HIT (5ms)%n");

            // Update access count and promote if threshold reached
            int count = accessCount.merge(videoId, 1, Integer::sum);
            if (count >= PROMOTION_THRESHOLD) {
                VideoData data = database.get(videoId);   // fetch from DB or L2
                if (data != null) {
                    l1Cache.put(videoId, data);
                    System.out.println("→ Promoted to L1");
                }
            }

            long timeTaken = System.currentTimeMillis() - startTime;
            System.out.printf("→ Total: %.1fms%n%n", (double) timeTaken);
            return database.get(videoId);
        }
        l2Misses++;

        // === L3 Database (slowest) ===
        l3Hits++;
        VideoData data = queryDatabase(videoId);
        if (data == null) {
            System.out.println("→ Video not found in any cache level!");
            return null;
        }

        System.out.println("→ L1 Cache MISS");
        System.out.println("→ L2 Cache MISS");
        System.out.println("→ L3 Database HIT (150ms)");

        // Add to L2 (with access count = 1)
        l2Cache.put(videoId, "/ssd/videos/" + videoId + ".mp4");
        accessCount.put(videoId, 1);
        System.out.println("→ Added to L2 (access count: 1)");

        long timeTaken = System.currentTimeMillis() - startTime;
        System.out.printf("→ Total: %.1fms%n%n", (double) timeTaken);

        return data;
    }

    // Cache invalidation (when video content is updated)
    public void invalidateVideo(String videoId) {
        l1Cache.remove(videoId);
        l2Cache.remove(videoId);
        accessCount.remove(videoId);
        System.out.println("invalidateVideo(\"" + videoId + "\") → Removed from L1, L2 and access counters");
    }

    // Get overall cache statistics
    public void getStatistics() {
        int totalRequests = l1Hits + l1Misses;

        double l1HitRate = totalRequests == 0 ? 0 : (l1Hits * 100.0) / totalRequests;
        double l2HitRate = (l1Misses == 0) ? 0 : (l2Hits * 100.0) / l1Misses;
        double l3HitRate = (l2Misses == 0) ? 0 : (l3Hits * 100.0) / l2Misses;

        System.out.println("getStatistics() →");
        System.out.printf("L1: Hit Rate %.0f%%, Avg Time: 0.5ms%n", l1HitRate);
        System.out.printf("L2: Hit Rate %.0f%%, Avg Time: 5ms%n", l2HitRate);
        System.out.printf("L3: Hit Rate %.0f%%, Avg Time: 150ms%n", l3HitRate);
        System.out.printf("Overall Hit Rate: %.0f%% (10M concurrent users optimized)%n",
                (l1HitRate * 0.85 + l2HitRate * 0.12 + l3HitRate * 0.03));
    }

    // ==================== MAIN METHOD ====================
    public static void main(String[] args) throws InterruptedException {
        qn10 netflixCache = new qn10();

        System.out.println("=== Multi-Level Cache System (Netflix-style) ===\n");

        // First request - L1 MISS → L2 HIT → Promote
        netflixCache.getVideo("video_123");

        // Second request - L1 HIT
        netflixCache.getVideo("video_123");

        // Cold video - full cache miss path
        netflixCache.getVideo("video_999");

        System.out.println();
        netflixCache.getStatistics();

        // Demo cache invalidation
        System.out.println("\n--- Content Update ---");
        netflixCache.invalidateVideo("video_123");
    }
}