// qn5.java - Problem: Transaction Log Search System (Linear + Binary Search)

import java.util.*;

public class qn5 {

    // ==================== USER DEFINED CLASS ====================
    static class TransactionLog {
        String accountId;
        String timestamp;
        double amount;

        TransactionLog(String accountId, String timestamp, double amount) {
            this.accountId = accountId;
            this.timestamp = timestamp;
            this.amount = amount;
        }

        @Override
        public String toString() {
            return accountId;
        }
    }

    // ==================== SEARCH SYSTEM ====================
    private TransactionLog[] logs;

    public qn5() {
        // Sample data (unsorted)
        logs = new TransactionLog[]{
                new TransactionLog("accB", "10:05", 1250.75),
                new TransactionLog("accA", "09:30", 850.00),
                new TransactionLog("accB", "11:15", 3200.50),
                new TransactionLog("accC", "10:45", 450.25)
        };
    }

    // ====================== LINEAR SEARCH ======================
    public void linearSearch(String target) {
        System.out.println("Linear Search for '" + target + "':");
        int firstIndex = -1;
        int lastIndex = -1;
        int comparisons = 0;

        for (int i = 0; i < logs.length; i++) {
            comparisons++;
            if (logs[i].accountId.equals(target)) {
                if (firstIndex == -1) firstIndex = i;
                lastIndex = i;
            }
        }

        if (firstIndex != -1) {
            System.out.println("First occurrence of " + target + ": index " + firstIndex
                    + " (" + comparisons + " comparisons)");
            System.out.println("Last occurrence of " + target + ": index " + lastIndex);
        } else {
            System.out.println(target + " not found (" + comparisons + " comparisons)");
        }
    }

    // ====================== BINARY SEARCH (requires sorted array) ======================
    // First sort the logs by accountId
    public void sortLogsByAccountId() {
        Arrays.sort(logs, (a, b) -> a.accountId.compareTo(b.accountId));
        System.out.println("Sorted logs: " + Arrays.toString(logs));
    }

    // Binary Search - Find first occurrence
    public int binarySearchFirst(String target) {
        int low = 0;
        int high = logs.length - 1;
        int result = -1;
        int comparisons = 0;

        while (low <= high) {
            comparisons++;
            int mid = low + (high - low) / 2;

            if (logs[mid].accountId.equals(target)) {
                result = mid;
                high = mid - 1;        // continue searching left for first occurrence
            }
            else if (logs[mid].accountId.compareTo(target) < 0) {
                low = mid + 1;
            }
            else {
                high = mid - 1;
            }
        }
        System.out.println("Binary Search for '" + target + "': index " + result
                + " (" + comparisons + " comparisons)");
        return result;
    }

    // Count total occurrences using Binary Search
    public int countOccurrences(String target) {
        int first = binarySearchFirst(target);
        if (first == -1) return 0;

        int count = 1;
        // Count to the right of first occurrence
        for (int i = first + 1; i < logs.length; i++) {
            if (logs[i].accountId.equals(target)) count++;
            else break;
        }
        return count;
    }

    // ==================== MAIN METHOD ====================
    public static void main(String[] args) {
        qn5 searcher = new qn5();

        System.out.println("=== Transaction Log Search System (Audit/Compliance) ===\n");

        System.out.println("Original logs: " + Arrays.toString(searcher.logs));
        System.out.println();

        // Linear Search
        searcher.linearSearch("accB");

        System.out.println("\n--- After Sorting ---");
        searcher.sortLogsByAccountId();

        // Binary Search
        int index = searcher.binarySearchFirst("accB");
        int count = searcher.countOccurrences("accB");

        if (index != -1) {
            System.out.println("Total occurrences of accB: " + count);
        }

        System.out.println("\nSummary:");
        System.out.println("• Linear Search : O(n) time");
        System.out.println("• Binary Search : O(log n) time (after sorting)");
    }
}