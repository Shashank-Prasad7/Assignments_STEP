// qn1.java - Banking Transaction Fee Sorting System

import java.util.*;
import java.time.*;

public class qn1 {

    // ==================== USER DEFINED CLASS ====================
    static class Transaction {
        String id;
        double fee;
        LocalTime timestamp;

        Transaction(String id, double fee, String timeStr) {
            this.id = id;
            this.fee = fee;
            this.timestamp = LocalTime.parse(timeStr);
        }

        @Override
        public String toString() {
            return id + ":" + fee + "@" + timestamp;
        }
    }

    // ==================== FEE SORTING SYSTEM ====================
    private final List<Transaction> transactions;

    public qn1() {
        this.transactions = new ArrayList<>();
    }

    public void addTransaction(String id, double fee, String timestamp) {
        transactions.add(new Transaction(id, fee, timestamp));
    }

    // ====================== BUBBLE SORT ======================
    // Used for small batches (<= 100 transactions)
    public void bubbleSortByFee() {
        int n = transactions.size();
        int passes = 0;
        int swaps = 0;
        boolean swapped;

        System.out.println("BubbleSort (fees):");

        for (int i = 0; i < n - 1; i++) {
            passes++;
            swapped = false;

            for (int j = 0; j < n - i - 1; j++) {
                if (transactions.get(j).fee > transactions.get(j + 1).fee) {
                    // Swap
                    Collections.swap(transactions, j, j + 1);
                    swaps++;
                    swapped = true;
                }
            }

            // Early termination if no swaps occurred
            if (!swapped) {
                break;
            }
        }

        // Print result
        System.out.print("[");
        for (int i = 0; i < transactions.size(); i++) {
            System.out.print(transactions.get(i).id + ":" + transactions.get(i).fee);
            if (i < transactions.size() - 1) System.out.print(", ");
        }
        System.out.println("] // " + passes + " passes, " + swaps + " swaps");
    }

    // ====================== INSERTION SORT ======================
    // Used for medium batches (100 - 1000 transactions) - sorts by fee then timestamp
    public void insertionSortByFeeAndTime() {
        int n = transactions.size();

        for (int i = 1; i < n; i++) {
            Transaction key = transactions.get(i);
            int j = i - 1;

            // Shift elements that are greater than key
            while (j >= 0 && shouldShift(transactions.get(j), key)) {
                transactions.set(j + 1, transactions.get(j));
                j--;
            }
            transactions.set(j + 1, key);
        }

        System.out.println("InsertionSort (fee+ts):");
        System.out.print("[");
        for (int i = 0; i < transactions.size(); i++) {
            Transaction t = transactions.get(i);
            System.out.print(t.id + ":" + t.fee + "@" + t.timestamp);
            if (i < transactions.size() - 1) System.out.print(", ");
        }
        System.out.println("]");
    }

    // Helper: Compare two transactions - first by fee, then by timestamp (for stability)
    private boolean shouldShift(Transaction a, Transaction b) {
        if (a.fee != b.fee) {
            return a.fee > b.fee;
        }
        return a.timestamp.isAfter(b.timestamp);   // earlier time comes first
    }

    // Flag high-fee outliers (> $50)
    public void flagHighFeeOutliers() {
        System.out.print("High-fee outliers: ");
        List<Transaction> outliers = new ArrayList<>();

        for (Transaction t : transactions) {
            if (t.fee > 50.0) {
                outliers.add(t);
            }
        }

        if (outliers.isEmpty()) {
            System.out.println("none");
        } else {
            System.out.println();
            for (Transaction t : outliers) {
                System.out.println("   ⚠️  " + t.id + " - $" + t.fee + " (High Fee Outlier)");
            }
        }
    }

    // ==================== MAIN METHOD ====================
    public static void main(String[] args) {
        qn1 sorter = new qn1();

        System.out.println("=== Banking Transaction Fee Sorting System ===\n");

        // Sample Input
        sorter.addTransaction("id1", 10.5, "10:00");
        sorter.addTransaction("id2", 25.0, "09:30");
        sorter.addTransaction("id3", 5.0,  "10:15");

        System.out.println("Input transactions:");
        for (Transaction t : sorter.transactions) {
            System.out.println(t.id + ", fee=" + t.fee + ", ts=" + t.timestamp);
        }
        System.out.println();

        // Bubble Sort (for small batches)
        sorter.bubbleSortByFee();

        // Insertion Sort (stable sort by fee + timestamp)
        sorter.insertionSortByFeeAndTime();

        // High-fee outlier detection
        sorter.flagHighFeeOutliers();

        System.out.println("\nSorting completed using Bubble Sort (small) and Insertion Sort (medium).");
    }
}