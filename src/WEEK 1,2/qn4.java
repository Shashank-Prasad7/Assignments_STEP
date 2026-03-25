// qn4.java - Problem 4: Plagiarism Detection System

import java.util.*;

public class qn4 {

    // ==================== USER DEFINED CLASS ====================
    // Custom class to represent each document
    static class Essay {
        String essayId;
        String content;

        Essay(String essayId, String content) {
            this.essayId = essayId;
            this.content = content;
        }
    }

    // ==================== PLAGIARISM DETECTOR CLASS ====================
    private final Map<String, Set<String>> ngramDatabase;

    public qn4() {
        this.ngramDatabase = new HashMap<>();
    }

    // Extract n-grams (sequences of n words)
    private List<String> getNGrams(String text, int n) {
        String cleanedText = text.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        String[] words = cleanedText.split("\\s+");
        List<String> ngrams = new ArrayList<>();

        for (int i = 0; i <= words.length - n; i++) {
            String ngram = String.join(" ", Arrays.copyOfRange(words, i, i + n));
            ngrams.add(ngram);
        }
        return ngrams;
    }

    // Add previous essay to the database
    public void addEssay(String essayId, String content, int nGramSize) {
        List<String> ngrams = getNGrams(content, nGramSize);
        for (String ngram : ngrams) {
            ngramDatabase.computeIfAbsent(ngram, k -> new HashSet<>()).add(essayId);
        }
    }

    // Analyze new student submission for plagiarism
    public void analyzeDocument(String newEssayId, String content, int nGramSize) {
        List<String> ngrams = getNGrams(content, nGramSize);

        System.out.println("analyzeDocument(\"" + newEssayId + ".txt\")");
        System.out.println("→ Extracted " + ngrams.size() + " n-grams");

        // Count how many n-grams match with each previous essay
        Map<String, Integer> matchCounter = new HashMap<>();

        for (String ngram : ngrams) {
            Set<String> matchingEssays = ngramDatabase.get(ngram);
            if (matchingEssays != null) {
                for (String essayId : matchingEssays) {
                    if (!essayId.equals(newEssayId)) {
                        matchCounter.merge(essayId, 1, Integer::sum);
                    }
                }
            }
        }

        // Display results with similarity percentage
        for (Map.Entry<String, Integer> entry : matchCounter.entrySet()) {
            String essayId = entry.getKey();
            int matchCount = entry.getValue();
            double similarity = (matchCount * 100.0) / ngrams.size();

            System.out.printf("→ Found %d matching n-grams with \"%s.txt\"%n", matchCount, essayId);
            System.out.printf("→ Similarity: %.1f%% ", similarity);

            if (similarity >= 50.0) {
                System.out.println("(PLAGIARISM DETECTED)");
            } else if (similarity >= 10.0) {
                System.out.println("(suspicious)");
            } else {
                System.out.println();
            }
        }

        if (matchCounter.isEmpty()) {
            System.out.println("→ No significant matches found.");
        }
    }

    // ==================== MAIN METHOD ====================
    public static void main(String[] args) {
        qn4 plagiarismDetector = new qn4();

        // Adding previous essays to the database
        plagiarismDetector.addEssay("essay_089",
                "this is a test document for plagiarism detection system using hash tables and n-grams", 5);

        plagiarismDetector.addEssay("essay_092",
                "this is a test document for plagiarism detection system using hash tables and n-grams " +
                        "along with advanced techniques for better accuracy and matching", 5);

        // Analyzing new student submission
        plagiarismDetector.analyzeDocument("essay_123",
                "this is a test document for plagiarism detection system using hash tables and n-grams", 5);
    }
}