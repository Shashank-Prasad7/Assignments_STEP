// qn4.java - Problem: Asset Returns Sorting System (Merge Sort + Quick Sort)

import java.util.*;

public class qn4 {

    // ==================== USER DEFINED CLASS ====================
    static class Asset {
        String symbol;
        double returnRate;     // in percentage
        double volatility;     // for secondary sorting

        Asset(String symbol, double returnRate, double volatility) {
            this.symbol = symbol;
            this.returnRate = returnRate;
            this.volatility = volatility;
        }

        @Override
        public String toString() {
            return symbol + ":" + returnRate + "%";
        }
    }

    // ==================== ASSET SORTING SYSTEM ====================
    private Asset[] assets;

    public qn4() {
        // Sample input
        assets = new Asset[]{
                new Asset("AAPL", 12.0, 25.5),
                new Asset("TSLA", 8.0, 45.0),
                new Asset("GOOG", 15.0, 18.0)
        };
    }

    // ====================== MERGE SORT (Ascending by returnRate - Stable) ======================
    public void mergeSortAscending() {
        Asset[] temp = new Asset[assets.length];
        mergeSortHelper(assets, temp, 0, assets.length - 1);

        System.out.println("Merge Sort (returnRate ascending - Stable):");
        printAssets();
    }

    private void mergeSortHelper(Asset[] arr, Asset[] temp, int left, int right) {
        if (left >= right) return;

        int mid = left + (right - left) / 2;
        mergeSortHelper(arr, temp, left, mid);
        mergeSortHelper(arr, temp, mid + 1, right);
        merge(arr, temp, left, mid, right);
    }

    private void merge(Asset[] arr, Asset[] temp, int left, int mid, int right) {
        for (int i = left; i <= right; i++) {
            temp[i] = arr[i];
        }

        int i = left, j = mid + 1, k = left;

        while (i <= mid && j <= right) {
            // Stable sort: if returns are equal, preserve original order
            if (temp[i].returnRate <= temp[j].returnRate) {
                arr[k++] = temp[i++];
            } else {
                arr[k++] = temp[j++];
            }
        }
        while (i <= mid) arr[k++] = temp[i++];
        while (j <= right) arr[k++] = temp[j++];
    }

    // ====================== QUICK SORT (Descending returnRate + Ascending volatility) ======================
    public void quickSortDescending() {
        quickSortHelper(assets, 0, assets.length - 1);

        System.out.println("\nQuick Sort (returnRate DESC + volatility ASC):");
        printAssets();
    }

    private void quickSortHelper(Asset[] arr, int low, int high) {
        if (low < high) {
            // Use median-of-3 pivot selection for better performance
            int pivotIndex = medianOfThree(arr, low, high);
            pivotIndex = partition(arr, low, high, pivotIndex);

            quickSortHelper(arr, low, pivotIndex - 1);
            quickSortHelper(arr, pivotIndex + 1, high);
        }
    }

    private int medianOfThree(Asset[] arr, int low, int high) {
        int mid = low + (high - low) / 2;

        if (arr[low].returnRate < arr[mid].returnRate) swap(arr, low, mid);
        if (arr[low].returnRate < arr[high].returnRate) swap(arr, low, high);
        if (arr[mid].returnRate < arr[high].returnRate) swap(arr, mid, high);

        return high;   // median is now at high
    }

    private int partition(Asset[] arr, int low, int high, int pivotIndex) {
        swap(arr, pivotIndex, high);   // move pivot to end
        Asset pivot = arr[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            // Primary: higher returnRate first (DESC)
            // Secondary: if return same, lower volatility first (ASC)
            boolean shouldSwap = false;

            if (arr[j].returnRate > pivot.returnRate) {
                shouldSwap = true;
            } else if (arr[j].returnRate == pivot.returnRate) {
                if (arr[j].volatility < pivot.volatility) {
                    shouldSwap = true;
                }
            }

            if (shouldSwap) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    private void swap(Asset[] arr, int i, int j) {
        Asset temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    // Helper to print current state of assets
    private void printAssets() {
        System.out.print("[");
        for (int i = 0; i < assets.length; i++) {
            System.out.print(assets[i]);
            if (i < assets.length - 1) System.out.print(", ");
        }
        System.out.println("]");
    }

    // ==================== MAIN METHOD ====================
    public static void main(String[] args) {
        qn4 sorter = new qn4();

        System.out.println("=== Asset Returns Sorting System ===\n");
        System.out.println("Input: [AAPL:12%, TSLA:8%, GOOG:15%]\n");

        // Merge Sort - Ascending (Stable)
        sorter.mergeSortAscending();

        // Quick Sort - Descending with secondary key
        sorter.quickSortDescending();

        System.out.println("\nBoth sorts completed successfully.");
        System.out.println("Merge Sort is stable and guarantees O(n log n).");
        System.out.println("Quick Sort uses median-of-3 pivot selection.");
    }
}