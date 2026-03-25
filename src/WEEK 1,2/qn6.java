// qn6.java - Problem 6: Distributed Rate Limiter for API Gateway

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class qn6 {

    // ==================== USER DEFINED CLASS ====================
    // Token Bucket implementation for rate limiting
    static class TokenBucket {
        private final int maxTokens;           // Maximum burst size (e.g., 1000)
        private final double refillRate;       // Tokens per second
        private double tokens;                 // Current tokens available
        private long lastRefillTime;           // Last time tokens were refilled

        public TokenBucket(int maxTokensPerHour) {
            this.maxTokens = maxTokensPerHour;
            this.refillRate = maxTokensPerHour / 3600.0;  // tokens per second
            this.tokens = maxTokensPerHour;               // start full
            this.lastRefillTime = System.currentTimeMillis();
        }

        // Try to consume one token (request)
        public synchronized boolean tryConsume() {
            refillTokens();
            if (tokens >= 1.0) {
                tokens -= 1.0;
                return true;
            }
            return false;
        }

        // Refill tokens based on time passed
        private void refillTokens() {
            long now = System.currentTimeMillis();
            long timePassed = now - lastRefillTime;

            if (timePassed > 0) {
                double tokensToAdd = timePassed * refillRate / 1000.0;  // convert ms to seconds
                tokens = Math.min(maxTokens, tokens + tokensToAdd);
                lastRefillTime = now;
            }
        }

        // Get remaining tokens
        public int getRemainingTokens() {
            refillTokens();
            return (int) Math.floor(tokens);
        }

        // Get time until next token is available (in seconds)
        public long getRetryAfterSeconds() {
            refillTokens();
            if (tokens >= 1.0) return 0;
            double tokensNeeded = 1.0 - tokens;
            return (long) Math.ceil(tokensNeeded / refillRate);
        }
    }

    // ==================== RATE LIMITER SYSTEM ====================
    private final Map<String, TokenBucket> clientBuckets;
    private final int requestsPerHour;

    public qn6(int requestsPerHour) {
        this.requestsPerHour = requestsPerHour;
        this.clientBuckets = new ConcurrentHashMap<>();   // Thread-safe for distributed use
    }

    // Check if request is allowed for a client
    public String checkRateLimit(String clientId) {
        TokenBucket bucket = clientBuckets.computeIfAbsent(clientId,
                k -> new TokenBucket(requestsPerHour));

        if (bucket.tryConsume()) {
            int remaining = bucket.getRemainingTokens();
            System.out.println("checkRateLimit(" + clientId + ") → Allowed (" + remaining + " requests remaining)");
            return "Allowed";
        } else {
            long retryAfter = bucket.getRetryAfterSeconds();
            System.out.println("checkRateLimit(" + clientId + ") → Denied (0 requests remaining, retry after " + retryAfter + "s)");
            return "Denied";
        }
    }

    // Get detailed rate limit status
    public void getRateLimitStatus(String clientId) {
        TokenBucket bucket = clientBuckets.get(clientId);
        if (bucket == null) {
            System.out.println("getRateLimitStatus(" + clientId + ") → No requests made yet.");
            return;
        }

        int remaining = bucket.getRemainingTokens();
        int used = requestsPerHour - remaining;
        long retryAfter = bucket.getRetryAfterSeconds();

        System.out.println("getRateLimitStatus(\"" + clientId + "\") → {used: " + used
                + ", limit: " + requestsPerHour
                + ", remaining: " + remaining
                + ", retryAfter: " + retryAfter + "s}");
    }

    // ==================== MAIN METHOD ====================
    public static void main(String[] args) throws InterruptedException {
        qn6 rateLimiter = new qn6(1000);   // 1000 requests per hour

        System.out.println("=== API Rate Limiter Demo ===\n");

        String clientId = "abc123";

        // Simulate multiple requests
        for (int i = 1; i <= 1005; i++) {
            rateLimiter.checkRateLimit(clientId);

            if (i == 5) {
                System.out.println("\n--- Taking a small break ---\n");
                Thread.sleep(2000);   // simulate time passing
            }

            if (i == 1003) {
                rateLimiter.getRateLimitStatus(clientId);
            }
        }
    }
}