// file name: qn6.java
import java.util.Arrays;

public class qn6 {

    // Linear search to find threshold match (unsorted approach simulation)
    public static int linearSearch(int[] arr, int target) {
        int comparisons = 0;
        for (int i = 0; i < arr.length; i++) {
            comparisons++;
            if (arr[i] == target) {
                System.out.println("Linear: threshold=" + target + " → found at index " + i +
                        " (" + comparisons + " comps)");
                return comparisons;
            }
        }
        System.out.println("Linear: threshold=" + target + " → not found (" + comparisons + " comps)");
        return comparisons;
    }

    // Binary search to find insertion point (lower_bound)
    public static int lowerBound(int[] arr, int target) {
        int left = 0, right = arr.length;
        int comparisons = 0;

        while (left < right) {
            int mid = left + (right - left) / 2;
            comparisons++;

            if (arr[mid] < target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        System.out.println("Lower bound (insertion point) for " + target + ": index " + left +
                " (" + comparisons + " comps)");
        return comparisons;
    }

    // Find floor value (largest element ≤ target)
    public static int findFloor(int[] arr, int target) {
        int left = 0, right = arr.length - 1;
        int floor = -1;
        int comparisons = 0;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            comparisons++;

            if (arr[mid] <= target) {
                floor = arr[mid];
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        System.out.println("Floor of " + target + ": " + (floor == -1 ? "none" : floor) +
                " (" + comparisons + " comps)");
        return comparisons;
    }

    // Find ceiling value (smallest element ≥ target)
    public static int findCeiling(int[] arr, int target) {
        int left = 0, right = arr.length - 1;
        int ceiling = -1;
        int comparisons = 0;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            comparisons++;

            if (arr[mid] >= target) {
                ceiling = arr[mid];
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        System.out.println("Ceiling of " + target + ": " + (ceiling == -1 ? "none" : ceiling) +
                " (" + comparisons + " comps)");
        return comparisons;
    }

    // Binary search to find exact match
    public static int binarySearch(int[] arr, int target) {
        int left = 0, right = arr.length - 1;
        int comparisons = 0;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            comparisons++;

            if (arr[mid] == target) {
                System.out.println("Binary: threshold=" + target + " → found at index " + mid +
                        " (" + comparisons + " comps)");
                return comparisons;
            } else if (arr[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        System.out.println("Binary: threshold=" + target + " → not found (" + comparisons + " comps)");
        return comparisons;
    }

    // Complete risk band analysis for a client
    public static void analyzeRiskBands(int[] sortedRisks, int clientRisk) {
        System.out.println("\n=== Risk Band Analysis for Client Risk Score: " + clientRisk + " ===");
        System.out.println("Sorted Risk Bands: " + Arrays.toString(sortedRisks));
        System.out.println();

        // Linear search (simulating unsorted search)
        linearSearch(sortedRisks, clientRisk);
        System.out.println();

        // Binary search operations
        binarySearch(sortedRisks, clientRisk);
        findFloor(sortedRisks, clientRisk);
        findCeiling(sortedRisks, clientRisk);
        lowerBound(sortedRisks, clientRisk);

        System.out.println();
    }

    // Demonstrate risk band assignment for new client
    public static void assignRiskBand(int[] sortedRisks, int clientRisk) {
        int insertionPoint = -1;
        int left = 0, right = sortedRisks.length;

        // Binary search to find insertion point
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (sortedRisks[mid] < clientRisk) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        insertionPoint = left;

        System.out.println("\n=== Risk Band Assignment for New Client (Risk: " + clientRisk + ") ===");

        if (insertionPoint == 0) {
            System.out.println("Client risk " + clientRisk + " is below all existing bands.");
            System.out.println("Recommended band: Below " + sortedRisks[0] + " (New lowest tier)");
        } else if (insertionPoint == sortedRisks.length) {
            System.out.println("Client risk " + clientRisk + " is above all existing bands.");
            System.out.println("Recommended band: Above " + sortedRisks[sortedRisks.length - 1] + " (New highest tier)");
        } else if (sortedRisks[insertionPoint] == clientRisk) {
            System.out.println("Client risk " + clientRisk + " exactly matches band: " + sortedRisks[insertionPoint]);
            System.out.println("Assigned to risk tier: " + sortedRisks[insertionPoint]);
        } else {
            System.out.println("Client risk " + clientRisk + " falls between bands:");
            System.out.println("  Lower band (floor): " + sortedRisks[insertionPoint - 1]);
            System.out.println("  Upper band (ceiling): " + sortedRisks[insertionPoint]);
            System.out.println("Assigned to risk tier based on compliance rules.");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        // Sample risk bands
        int[] riskBands = {10, 25, 50, 100};

        System.out.println("=== Dynamic Risk Pricing Table ===");
        System.out.println("Risk Bands: " + Arrays.toString(riskBands));
        System.out.println("These bands represent risk thresholds for pricing tiers.");
        System.out.println();

        // Test case 1: Client with risk score 30
        analyzeRiskBands(riskBands, 30);
        assignRiskBand(riskBands, 30);

        // Test case 2: Client with risk score 25 (exact match)
        analyzeRiskBands(riskBands, 25);
        assignRiskBand(riskBands, 25);

        // Test case 3: Client with risk score 5 (below lowest)
        analyzeRiskBands(riskBands, 5);
        assignRiskBand(riskBands, 5);

        // Test case 4: Client with risk score 150 (above highest)
        analyzeRiskBands(riskBands, 150);
        assignRiskBand(riskBands, 150);

        // Demonstrate compliance band assignment for multiple clients
        System.out.println("=== Compliance Band Assignment for Multiple Clients ===");
        int[] clientRisks = {12, 35, 75, 25, 90, 45};

        for (int clientRisk : clientRisks) {
            int floor = -1, ceiling = -1;

            // Find floor
            int left = 0, right = riskBands.length - 1;
            while (left <= right) {
                int mid = left + (right - left) / 2;
                if (riskBands[mid] <= clientRisk) {
                    floor = riskBands[mid];
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }

            // Find ceiling
            left = 0;
            right = riskBands.length - 1;
            while (left <= right) {
                int mid = left + (right - left) / 2;
                if (riskBands[mid] >= clientRisk) {
                    ceiling = riskBands[mid];
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            }

            System.out.printf("Client Risk: %3d | Floor: %3s | Ceiling: %3s | Compliance Band: ",
                    clientRisk,
                    floor == -1 ? "N/A" : String.valueOf(floor),
                    ceiling == -1 ? "N/A" : String.valueOf(ceiling));

            if (floor == -1) {
                System.out.println("Below " + riskBands[0]);
            } else if (ceiling == -1) {
                System.out.println("Above " + riskBands[riskBands.length - 1]);
            } else if (floor == ceiling) {
                System.out.println("Exactly at band " + floor);
            } else {
                System.out.println(floor + " - " + ceiling);
            }
        }

        // Performance comparison
        System.out.println("\n=== Performance Comparison ===");
        System.out.println("For 4-element array, worst-case comparisons:");
        System.out.println("  Linear Search: 4 comparisons");
        System.out.println("  Binary Search: 3 comparisons (log₂4 = 2-3 comparisons)");
        System.out.println("\nFor larger datasets, binary search provides O(log n) vs O(n) for linear search.");
        System.out.println("This is crucial for dynamic risk pricing tables with thousands of bands.");
    }
}