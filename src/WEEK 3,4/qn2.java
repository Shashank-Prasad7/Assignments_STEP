// qn2.java - Problem: Client Risk Score Sorting System

import java.util.*;

public class qn2 {

    // ==================== USER DEFINED CLASS ====================
    static class Client {
        String name;
        int riskScore;
        double accountBalance;

        Client(String name, int riskScore, double accountBalance) {
            this.name = name;
            this.riskScore = riskScore;
            this.accountBalance = accountBalance;
        }

        @Override
        public String toString() {
            return name + "(" + riskScore + ")";
        }
    }

    // ==================== RISK SORTING SYSTEM ====================
    private Client[] clients;

    public qn2() {
        // Sample data
        clients = new Client[]{
                new Client("clientC", 80, 15000.50),
                new Client("clientA", 20, 45000.75),
                new Client("clientB", 50, 22000.00)
        };
    }

    // ====================== BUBBLE SORT (Ascending by riskScore) ======================
    public void bubbleSortAscending() {
        int n = clients.length;
        int swaps = 0;

        System.out.println("Bubble Sort (riskScore ascending):");

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;

            for (int j = 0; j < n - i - 1; j++) {
                if (clients[j].riskScore > clients[j + 1].riskScore) {
                    // Swap
                    Client temp = clients[j];
                    clients[j] = clients[j + 1];
                    clients[j + 1] = temp;
                    swaps++;
                    swapped = true;

                    // Visualize swap for demo
                    System.out.println("   Swap: " + clients[j + 1] + " ↔ " + clients[j]);
                }
            }
            if (!swapped) break;   // Early termination (adaptive)
        }

        System.out.print("Result: [");
        for (int i = 0; i < n; i++) {
            System.out.print(clients[i]);
            if (i < n - 1) System.out.print(", ");
        }
        System.out.println("] // Swaps: " + swaps);
    }

    // ====================== INSERTION SORT (Descending by riskScore, then by balance) ======================
    public void insertionSortDescending() {
        int n = clients.length;

        for (int i = 1; i < n; i++) {
            Client key = clients[i];
            int j = i - 1;

            // Shift elements that are smaller than key (for descending order)
            while (j >= 0 && shouldShift(clients[j], key)) {
                clients[j + 1] = clients[j];
                j--;
            }
            clients[j + 1] = key;
        }

        System.out.println("\nInsertion Sort (riskScore DESC + balance):");
        System.out.print("Result: [");
        for (int i = 0; i < n; i++) {
            System.out.print(clients[i]);
            if (i < n - 1) System.out.print(", ");
        }
        System.out.println("]");
    }

    // Helper for Insertion Sort: Compare for descending riskScore, then higher balance
    private boolean shouldShift(Client a, Client b) {
        if (a.riskScore != b.riskScore) {
            return a.riskScore < b.riskScore;   // higher risk first
        }
        return a.accountBalance < b.accountBalance; // if risk same, higher balance first
    }

    // ====================== TOP 10 HIGHEST RISK CLIENTS ======================
    public void showTopRiskClients(int topN) {
        System.out.println("\nTop " + topN + " highest risk clients:");
        // Since we have Insertion Sort in descending order, first 'topN' are highest risk
        for (int i = 0; i < Math.min(topN, clients.length); i++) {
            System.out.println("   " + (i + 1) + ". " + clients[i]);
        }
    }

    // ==================== MAIN METHOD ====================
    public static void main(String[] args) {
        qn2 riskSorter = new qn2();

        System.out.println("=== Client Risk Score Sorting System ===\n");

        System.out.println("Input: [clientC:80, clientA:20, clientB:50]");
        System.out.println();

        // Bubble Sort - Ascending
        riskSorter.bubbleSortAscending();

        // Insertion Sort - Descending by risk + balance
        riskSorter.insertionSortDescending();

        // Show Top highest risk clients
        riskSorter.showTopRiskClients(3);

        System.out.println("\nBoth sorts are in-place (O(1) extra space) as required.");
    }
}