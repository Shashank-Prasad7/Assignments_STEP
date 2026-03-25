// qn8.java - Problem 8: Parking Lot Management with Open Addressing

import java.util.*;
import java.time.*;

public class qn8 {

    // ==================== USER DEFINED CLASS ====================
    // Represents a parked vehicle with entry time
    static class ParkedVehicle {
        String licensePlate;
        LocalDateTime entryTime;
        int spotNumber;

        ParkedVehicle(String licensePlate, int spotNumber) {
            this.licensePlate = licensePlate;
            this.entryTime = LocalDateTime.now();
            this.spotNumber = spotNumber;
        }
    }

    // Spot status
    enum SpotStatus {
        EMPTY, OCCUPIED, DELETED
    }

    // ==================== PARKING LOT SYSTEM (Open Addressing) ====================
    private final ParkedVehicle[] parkingSpots;
    private final SpotStatus[] status;
    private final int capacity;
    private int occupiedCount = 0;
    private double totalProbes = 0;
    private int totalParkings = 0;

    public qn8(int capacity) {
        this.capacity = capacity;
        this.parkingSpots = new ParkedVehicle[capacity];
        this.status = new SpotStatus[capacity];
        Arrays.fill(status, SpotStatus.EMPTY);
    }

    // Custom hash function: license plate -> preferred spot
    private int hash(String licensePlate) {
        int hash = 0;
        for (char c : licensePlate.toCharArray()) {
            hash = (hash * 31 + c) % capacity;
        }
        return Math.abs(hash) % capacity;
    }

    // Park vehicle using Linear Probing
    public void parkVehicle(String licensePlate) {
        if (occupiedCount >= capacity) {
            System.out.println("Parking full! Cannot park " + licensePlate);
            return;
        }

        int preferredSpot = hash(licensePlate);
        int currentSpot = preferredSpot;
        int probes = 0;

        while (status[currentSpot] == SpotStatus.OCCUPIED) {
            probes++;
            currentSpot = (currentSpot + 1) % capacity;   // Linear Probing
            if (currentSpot == preferredSpot) { // full circle
                System.out.println("Parking full! Cannot park " + licensePlate);
                return;
            }
        }

        // Park the vehicle
        parkingSpots[currentSpot] = new ParkedVehicle(licensePlate, currentSpot);
        status[currentSpot] = SpotStatus.OCCUPIED;
        occupiedCount++;

        totalProbes += probes;
        totalParkings++;

        System.out.printf("parkVehicle(\"%s\") → Assigned spot #%d (%d probes)%n",
                licensePlate, currentSpot, probes);
    }

    // Exit vehicle and calculate billing
    public void exitVehicle(String licensePlate) {
        for (int i = 0; i < capacity; i++) {
            if (status[i] == SpotStatus.OCCUPIED &&
                    parkingSpots[i] != null &&
                    parkingSpots[i].licensePlate.equals(licensePlate)) {

                ParkedVehicle vehicle = parkingSpots[i];
                Duration duration = Duration.between(vehicle.entryTime, LocalDateTime.now());
                long hours = duration.toHours();
                long minutes = duration.toMinutesPart();

                // Simple fee: $5 per hour + $0.10 per minute
                double fee = (hours * 5.0) + (minutes * 0.10);

                System.out.printf("exitVehicle(\"%s\") → Spot #%d freed, Duration: %dh %dm, Fee: $%.2f%n",
                        licensePlate, vehicle.spotNumber, hours, minutes, fee);

                // Mark spot as deleted (for open addressing)
                status[i] = SpotStatus.DELETED;
                parkingSpots[i] = null;
                occupiedCount--;
                return;
            }
        }
        System.out.println("Vehicle " + licensePlate + " not found in parking lot.");
    }

    // Find nearest available spot to entrance (spot 0 is entrance)
    public int findNearestAvailableSpot() {
        for (int i = 0; i < capacity; i++) {
            if (status[i] != SpotStatus.OCCUPIED) {
                return i;
            }
        }
        return -1; // no spot available
    }

    // Generate parking statistics
    public void getStatistics() {
        double occupancy = (occupiedCount * 100.0) / capacity;
        double avgProbes = totalParkings == 0 ? 0 : totalProbes / totalParkings;

        System.out.println("getStatistics() →");
        System.out.printf("Occupancy: %.0f%%, Avg Probes: %.1f%n", occupancy, avgProbes);
        System.out.println("Total Spots: " + capacity + ", Occupied: " + occupiedCount);

        int nearest = findNearestAvailableSpot();
        if (nearest != -1) {
            System.out.println("Nearest available spot to entrance: #" + nearest);
        }
    }

    // ==================== MAIN METHOD ====================
    public static void main(String[] args) throws InterruptedException {
        qn8 parkingLot = new qn8(500);   // 500 parking spots

        System.out.println("=== Smart Parking Lot Management System ===\n");

        parkingLot.parkVehicle("ABC-1234");
        parkingLot.parkVehicle("ABC-1235");
        parkingLot.parkVehicle("XYZ-9999");
        parkingLot.parkVehicle("DEF-7777");

        Thread.sleep(2000); // simulate time passing

        parkingLot.exitVehicle("ABC-1234");

        System.out.println();
        parkingLot.getStatistics();
    }
}