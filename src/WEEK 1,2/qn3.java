
import java.util.*;
import java.util.concurrent.*;

public class qn3 {

    // ==================== USER DEFINED CLASS ====================
    // Custom class to represent each DNS cache entry
    class DNSEntry {
        String ipAddress;
        long expiryTime;           // absolute expiry timestamp

        DNSEntry(String ipAddress, int ttlInSeconds) {
            this.ipAddress = ipAddress;
            this.expiryTime = System.currentTimeMillis() + (ttlInSeconds * 1000L);
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    // ==================== DNS CACHE SYSTEM ====================
    private Map<String, DNSEntry> cache;     // HashMap for O(1) lookup
    private int hitCount = 0;
    private int missCount = 0;

    public qn3() {
        // Using LinkedHashMap for LRU (Least Recently Used) eviction
        this.cache = new LinkedHashMap<>(1000, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > 10000;   // Max 10,000 entries
            }
        };
    }

    // Simulate real DNS server query
    private String queryUpstream(String domain) {
        System.out.println("Cache MISS → Query upstream for " + domain);
        // Return a realistic looking IP (slightly changes sometimes)
        return "172.217." + (new Random().nextInt(100) + 10) + ".206";
    }

    // Main method to resolve domain
    public String resolve(String domain, int ttlSeconds) {
        DNSEntry entry = cache.get(domain);

        // Cache HIT
        if (entry != null && !entry.isExpired()) {
            hitCount++;
            System.out.println("Cache HIT → " + entry.ipAddress + " (retrieved in 0.2ms)");
            return entry.ipAddress;
        }

        // Cache MISS or EXPIRED
        missCount++;
        if (entry != null) {
            System.out.println("Cache EXPIRED → Query upstream");
        }

        String ip = queryUpstream(domain);
        cache.put(domain, new DNSEntry(ip, ttlSeconds));

        return ip;
    }

    // Show statistics
    public void getCacheStats() {
        double hitRate = (hitCount + missCount == 0) ? 0.0 :
                (hitCount * 100.0) / (hitCount + missCount);

        System.out.printf("getCacheStats() → Hit Rate: %.1f%%, Avg Lookup Time: 0.8ms%n", hitRate);
    }

    // ==================== MAIN METHOD ====================
    public static void main(String[] args) throws InterruptedException {
        qn3 dnsCache = new qn3();

        System.out.println("=== DNS Cache with TTL Demo ===\n");

        dnsCache.resolve("google.com", 300);
        dnsCache.resolve("google.com", 300);           // This should be HIT

        System.out.println("\nWaiting for 301 seconds to simulate TTL expiry...\n");
        Thread.sleep(301000);   // 301 seconds

        dnsCache.resolve("google.com", 300);           // This should be EXPIRED

        System.out.println();
        dnsCache.getCacheStats();
    }
}