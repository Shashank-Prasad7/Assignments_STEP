import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class qn2 {

    // Thread-safe stock map: productId -> stockCount
    private ConcurrentHashMap<String, AtomicInteger> stockMap = new ConcurrentHashMap<>();

    // Waiting list for each product: productId -> queue of userIds
    private ConcurrentHashMap<String, LinkedBlockingQueue<Integer>> waitingList = new ConcurrentHashMap<>();

    private Scanner sc = new Scanner(System.in);

    // Constructor: initialize some products with stock
    public qn2() {
        stockMap.put("IPHONE15_256GB", new AtomicInteger(100));
        stockMap.put("PS5_CONSOLE", new AtomicInteger(50));
        stockMap.put("LIMITED_SNEAKERS", new AtomicInteger(30));
    }

    // Check stock availability
    public int checkStock(String productId) {
        AtomicInteger stock = stockMap.get(productId);
        if (stock == null) {
            System.out.println("Product not found!");
            return -1;
        }
        return stock.get();
    }

    // Purchase item
    public void purchaseItem(String productId, int userId) {
        stockMap.computeIfAbsent(productId, k -> new AtomicInteger(0));
        waitingList.computeIfAbsent(productId, k -> new LinkedBlockingQueue<>());

        AtomicInteger stock = stockMap.get(productId);
        boolean purchased = false;

        // Synchronize stock decrement per product to prevent overselling
        synchronized (stock) {
            if (stock.get() > 0) {
                stock.decrementAndGet();
                purchased = true;
            }
        }

        if (purchased) {
            System.out.println("User #" + userId + " purchased " + productId + ". Remaining stock: " + stock.get());
        } else {
            // Add user to waiting list
            waitingList.get(productId).add(userId);
            System.out.println("User #" + userId + " added to waiting list for " + productId + ". Position: "
                    + waitingList.get(productId).size());
        }
    }

    // Process waiting list if stock is replenished
    public void replenishStock(String productId, int quantity) {
        stockMap.computeIfAbsent(productId, k -> new AtomicInteger(0));
        waitingList.computeIfAbsent(productId, k -> new LinkedBlockingQueue<>());

        AtomicInteger stock = stockMap.get(productId);
        stock.addAndGet(quantity);
        System.out.println(quantity + " units added. Current stock: " + stock.get());

        // Fulfill waiting list
        LinkedBlockingQueue<Integer> queue = waitingList.get(productId);
        while (stock.get() > 0 && !queue.isEmpty()) {
            int userId = queue.poll();
            synchronized (stock) {
                if (stock.get() > 0) {
                    stock.decrementAndGet();
                    System.out.println("Waiting list user #" + userId + " purchased " + productId
                            + ". Remaining stock: " + stock.get());
                } else {
                    // If stock runs out mid-processing, put user back
                    queue.add(userId);
                }
            }
        }
    }

    // User interaction loop
    public void start() {
        while (true) {
            System.out.println("\n--- Inventory Management System ---");
            System.out.println("1. Check Stock");
            System.out.println("2. Purchase Item");
            System.out.println("3. Replenish Stock");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");

            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter productId: ");
                    String productCheck = sc.nextLine();
                    int stock = checkStock(productCheck);
                    if (stock >= 0) {
                        System.out.println("Stock for " + productCheck + ": " + stock);
                    }
                    break;

                case 2:
                    System.out.print("Enter productId: ");
                    String productPurchase = sc.nextLine();
                    System.out.print("Enter userId: ");
                    int userId = sc.nextInt();
                    sc.nextLine(); // consume newline
                    purchaseItem(productPurchase, userId);
                    break;

                case 3:
                    System.out.print("Enter productId to replenish: ");
                    String productReplenish = sc.nextLine();
                    System.out.print("Enter quantity to add: ");
                    int quantity = sc.nextInt();
                    sc.nextLine(); // consume newline
                    replenishStock(productReplenish, quantity);
                    break;

                case 4:
                    System.out.println("Exiting... Goodbye!");
                    return;

                default:
                    System.out.println("Invalid option, try again.");
            }
        }
    }

    public static void main(String[] args) {
        qn2 inventory = new qn2();
        inventory.start();
    }
}