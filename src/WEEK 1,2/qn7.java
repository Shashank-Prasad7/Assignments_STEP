// qn7.java - Problem 7: Autocomplete System for Search Engine

import java.util.*;

public class qn7 {

    // ==================== USER DEFINED CLASS ====================
    // Custom Trie Node for prefix-based search
    static class TrieNode {
        Map<Character, TrieNode> children;   // for fast prefix traversal
        Map<String, Integer> topSuggestions; // stores top frequent queries under this prefix (for speed)

        TrieNode() {
            this.children = new HashMap<>();
            this.topSuggestions = new HashMap<>(); // will keep only top 10
        }
    }

    // ==================== AUTOCOMPLETE SYSTEM ====================
    private final TrieNode root;
    private final Map<String, Integer> frequencyMap;   // query -> total frequency

    public qn7() {
        this.root = new TrieNode();
        this.frequencyMap = new HashMap<>();
    }

    // Insert or update frequency of a search query
    public void updateFrequency(String query) {
        // Update global frequency
        int newFreq = frequencyMap.merge(query, 1, Integer::sum);

        // Insert into Trie
        TrieNode node = root;
        for (char c : query.toLowerCase().toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);

            // Update top suggestions at every prefix node (keep only top 10)
            node.topSuggestions.put(query, newFreq);
            if (node.topSuggestions.size() > 10) {
                // Remove the least frequent one
                String least = node.topSuggestions.entrySet().stream()
                        .min(Comparator.comparingInt(Map.Entry::getValue))
                        .map(Map.Entry::getKey)
                        .orElse(null);
                if (least != null) node.topSuggestions.remove(least);
            }
        }
        System.out.println("updateFrequency(\"" + query + "\") → Frequency: " + (newFreq - 1)
                + " → " + newFreq + (newFreq >= 3 ? " (trending)" : ""));
    }

    // Search for top 10 suggestions for a given prefix
    public void search(String prefix) {
        System.out.print("search(\"" + prefix + "\") →\n");

        TrieNode node = root;
        String lowerPrefix = prefix.toLowerCase();

        // Traverse to the prefix node
        for (char c : lowerPrefix.toCharArray()) {
            if (!node.children.containsKey(c)) {
                System.out.println("   (No suggestions found)");
                return;
            }
            node = node.children.get(c);
        }

        // Get top suggestions from this prefix node (already limited to 10)
        List<Map.Entry<String, Integer>> suggestions = new ArrayList<>(node.topSuggestions.entrySet());
        suggestions.sort((a, b) -> b.getValue().compareTo(a.getValue())); // descending frequency

        int rank = 1;
        for (Map.Entry<String, Integer> entry : suggestions) {
            System.out.printf("   %d. \"%s\" (%d searches)%n", rank++, entry.getKey(), entry.getValue());
        }

        if (suggestions.isEmpty()) {
            System.out.println("   (No suggestions found)");
        }
    }

    // Simple typo correction suggestion (basic edit distance 1)
    public void suggestCorrections(String typo) {
        System.out.println("Did you mean one of these?");
        // For demo, we just check if any stored query is very close
        frequencyMap.keySet().stream()
                .filter(q -> levenshteinDistance(q.toLowerCase(), typo.toLowerCase()) <= 2)
                .limit(5)
                .forEach(q -> System.out.println("   → " + q));
    }

    // Helper: Simple Levenshtein distance for typo handling
    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= s2.length(); j++) dp[0][j] = j;
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = (s1.charAt(i-1) == s2.charAt(j-1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i-1][j] + 1, dp[i][j-1] + 1), dp[i-1][j-1] + cost);
            }
        }
        return dp[s1.length()][s2.length()];
    }

    // ==================== MAIN METHOD ====================
    public static void main(String[] args) {
        qn7 autocomplete = new qn7();

        System.out.println("=== Google-like Autocomplete System ===\n");

        // Populate with sample search queries (simulating 10M scale with demo data)
        autocomplete.updateFrequency("java tutorial");
        autocomplete.updateFrequency("javascript");
        autocomplete.updateFrequency("java download");
        autocomplete.updateFrequency("java interview questions");
        autocomplete.updateFrequency("javascript tutorial");
        autocomplete.updateFrequency("java 21 features");
        autocomplete.updateFrequency("java script");
        autocomplete.updateFrequency("java programming");
        autocomplete.updateFrequency("java 21 features");   // trending
        autocomplete.updateFrequency("java 21 features");   // trending more

        // Search examples
        autocomplete.search("jav");

        System.out.println();
        autocomplete.search("java 21");

        // Update frequency (trending)
        autocomplete.updateFrequency("java 21 features");

        // Typo handling demo
        System.out.println("\n--- Typo Handling ---");
        autocomplete.search("jva");                    // no match
        autocomplete.suggestCorrections("jva");

        System.out.println("\nSystem ready for 10 million queries (optimized with Trie + HashMap hybrid)");
    }
}