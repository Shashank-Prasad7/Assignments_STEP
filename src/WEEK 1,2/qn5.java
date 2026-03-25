// qn5.java - Problem 5: Real-Time Analytics Dashboard for Website Traffic

import java.util.*;
import java.util.concurrent.*;

public class qn5 {

    // ==================== USER DEFINED CLASS ====================
    // Represents a single page view event
    static class PageViewEvent {
        String url;
        String userId;
        String source;

        PageViewEvent(String url, String userId, String source) {
            this.url = url;
            this.userId = userId;
            this.source = source;
        }
    }

    // ==================== REAL-TIME ANALYTICS DASHBOARD ====================
    private final Map<String, Integer> pageViewCount;           // url -> total views
    private final Map<String, Set<String>> uniqueVisitors;      // url -> set of userIds
    private final Map<String, Integer> trafficSourceCount;      // source -> count

    public qn5() {
        this.pageViewCount = new HashMap<>();
        this.uniqueVisitors = new HashMap<>();
        this.trafficSourceCount = new HashMap<>();
    }

    // Process incoming page view event in real-time
    public void processEvent(String url, String userId, String source) {
        // 1. Update total page views
        pageViewCount.merge(url, 1, Integer::sum);

        // 2. Track unique visitors per page
        uniqueVisitors.computeIfAbsent(url, k -> new HashSet<>()).add(userId);

        // 3. Track traffic sources
        trafficSourceCount.merge(source, 1, Integer::sum);
    }

    // Display real-time dashboard
    public void getDashboard() {
        System.out.println("getDashboard() →");

        // Get top 10 pages sorted by view count (descending)
        List<Map.Entry<String, Integer>> topPages = pageViewCount.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(10)
                .toList();

        System.out.println("Top Pages:");
        int rank = 1;
        for (Map.Entry<String, Integer> entry : topPages) {
            String url = entry.getKey();
            int views = entry.getValue();
            int unique = uniqueVisitors.getOrDefault(url, Collections.emptySet()).size();

            System.out.printf("%d. %s - %,d views (%,d unique)%n",
                    rank++, url, views, unique);
        }

        // Traffic Sources breakdown
        System.out.println("\nTraffic Sources:");
        int totalVisits = trafficSourceCount.values().stream().mapToInt(Integer::intValue).sum();

        if (totalVisits > 0) {
            trafficSourceCount.entrySet().stream()
                    .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                    .forEach(entry -> {
                        int percentage = (int) Math.round(entry.getValue() * 100.0 / totalVisits);
                        System.out.printf("%s: %d%%  ", entry.getKey(), percentage);
                    });
        }
        System.out.println();
    }

    // ==================== MAIN METHOD FOR DEMO ====================
    public static void main(String[] args) throws InterruptedException {
        qn5 analytics = new qn5();

        System.out.println("=== Real-Time Analytics Dashboard ===\n");

        // Simulate incoming traffic
        analytics.processEvent("/article/breaking-news", "user_123", "google");
        analytics.processEvent("/article/breaking-news", "user_456", "facebook");
        analytics.processEvent("/article/breaking-news", "user_789", "google");
        analytics.processEvent("/sports/championship", "user_101", "direct");
        analytics.processEvent("/sports/championship", "user_102", "facebook");
        analytics.processEvent("/article/breaking-news", "user_555", "google");
        analytics.processEvent("/tech/ai-update", "user_200", "google");

        // Show dashboard
        analytics.getDashboard();

        // Simulate more traffic
        System.out.println("\n--- After more page views ---\n");
        analytics.processEvent("/article/breaking-news", "user_300", "direct");
        analytics.processEvent("/sports/championship", "user_301", "google");

        analytics.getDashboard();
    }
}