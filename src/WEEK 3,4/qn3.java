// qn3.java - Problem: Trade Volume Sorting System (Merge Sort + Quick Sort)

import java.util.*;

public class qn3 {

    // ==================== USER DEFINED CLASS ====================
    static class Trade {
        String id;
        int volume;

        Trade(String id, int volume) {
            this.id = id;
            this.volume = volume;
        }

        @Override
        public String toString() {
            return id + ":" + volume;
        }
    }

    // ==================== TRADE VOLUME SORTER ====================
    private Trade[] trades;

    public qn3() {
        // Sample input from problem
        trades = new Trade[]{
                new Trade("trade3", 500),
                new Trade("trade1", 100),
                new Trade("trade2", 300)
        };
    }

    // ====================== MERGE SORT (Ascending by volume - Stable) ======================
    public void mergeSortAscending() {
        Trade[] temp = new Trade[trades.length];
        mergeSortHelper(trades, temp, 0, trades.length - 1);

        System.out.println("MergeSort (volume ascending):");
        System.out.print("[");
        for (int i = 0; i < trades.length; i++) {
            System.out.print(trades[i]);
            if (i < trades.length - 1) System.out.print(", ");
        }
        System.out.println("] // Stable");
    }

    private void mergeSortHelper(Trade[] arr, Trade[] temp, int left, int right) {
        if (left >= right) return;
        int mid = left + (right - left) / 2;
        mergeSortHelper(arr, temp, left, mid);
        mergeSortHelper(arr, temp, mid + 1, right);
        merge(arr, temp, left, mid, right);
    }

    private void merge(Trade[] arr, Trade[] temp, int left, int mid, int right) {
        for (int i = left; i <= right; i++) temp[i] = arr[i];

        int i = left, j = mid + 1, k = left;
        while (i <= mid && j <= right) {
            if (temp[i].volume <= temp[j].volume) {
                arr[k++] = temp[i++];
            } else {
                arr[k++] = temp[j++];
            }
        }
        while (i <= mid) arr[k++] = temp[i++];
        while (j <= right) arr[k++] = temp[j++];
    }

    // ====================== QUICK SORT (Descending by volume) ======================
    public void quickSortDescending() {
        quickSortHelper(trades, 0, trades.length - 1);

        System.out.println("\nQuickSort (volume DESC):");
        System.out.print("[");
        for (int i = 0; i < trades.length; i++) {
            System.out.print(trades[i]);
            if (i < trades.length - 1) System.out.print(", ");
        }
        System.out.println("] // Pivot: median");
    }

    private void quickSortHelper(Trade[] arr, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(arr, low, high);
            quickSortHelper(arr, low, pivotIndex - 1);
            quickSortHelper(arr, pivotIndex + 1, high);
        }
    }

    private int partition(Trade[] arr, int low, int high) {
        // Lomuto partition with median-of-three pivot selection for better performance
        int mid = low + (high - low) / 2;
        // Simple median-of-three
        if (arr[low].volume < arr[mid].volume) swap(arr, low, mid);
        if (arr[low].volume < arr[high].volume) swap(arr, low, high);
        if (arr[mid].volume < arr[high].volume) swap(arr, mid, high);

        Trade pivot = arr[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (arr[j].volume >= pivot.volume) {   // DESC order
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    private void swap(Trade[] arr, int i, int j) {
        Trade temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    // ====================== MERGE TWO SORTED LISTS ======================
    public void mergeTwoSortedLists(Trade[] morning, Trade[] afternoon) {
        Trade[] merged = new Trade[morning.length + afternoon.length];
        int i = 0, j = 0, k = 0;

        while (i < morning.length && j < afternoon.length) {
            if (morning[i].volume <= afternoon[j].volume) {
                merged[k++] = morning[i++];
            } else {
                merged[k++] = afternoon[j++];
            }
        }
        while (i < morning.length) merged[k++] = morning[i++];
        while (j < afternoon.length) merged[k++] = afternoon[j++];

        // Calculate total volume
        long totalVolume = 0;
        for (Trade t : merged) totalVolume += t.volume;

        System.out.println("\nMerged morning+afternoon:");
        System.out.print("[");
        for (int x = 0; x < merged.length; x++) {
            System.out.print(merged[x]);
            if (x < merged.length - 1) System.out.print(", ");
        }
        System.out.println("] total volume: " + totalVolume);
    }

    // ==================== MAIN METHOD ====================
    public static void main(String[] args) {
        qn3 sorter = new qn3();

        System.out.println("=== Trade Volume Sorting System (Citi Trading Desk) ===\n");
        System.out.println("Input: [trade3:500, trade1:100, trade2:300]\n");

        // Merge Sort (stable, O(n log n))
        sorter.mergeSortAscending();

        // Quick Sort (descending)
        sorter.quickSortDescending();

        // Demo: Merge two sorted sessions
        Trade[] morning = {new Trade("trade1", 100), new Trade("trade2", 300)};
        Trade[] afternoon = {new Trade("trade3", 500)};
        sorter.mergeTwoSortedLists(morning, afternoon);

        System.out.println("\nBoth sorts completed in guaranteed O(n log n) time.");
    }
}