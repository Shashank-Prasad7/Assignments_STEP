import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class qn1 {

    // Thread-safe map for usernames (username -> userId)
    private ConcurrentHashMap<String, Integer> usernameMap = new ConcurrentHashMap<>();

    // Thread-safe map for attempt frequency (username -> attempts)
    private ConcurrentHashMap<String, AtomicInteger> attemptFrequency = new ConcurrentHashMap<>();

    private Scanner sc = new Scanner(System.in);
    private int userIdCounter = 1; // to assign user IDs dynamically

    // Constructor to simulate some existing users
    public qn1() {
        List<String> existingUsers = Arrays.asList("john_doe", "admin", "jane_smith");
        for (String user : existingUsers) {
            usernameMap.put(user, userIdCounter++);
        }
    }

    // Check if username is available
    public boolean checkAvailability(String username) {
        attemptFrequency.computeIfAbsent(username, k -> new AtomicInteger(0)).incrementAndGet();
        return !usernameMap.containsKey(username);
    }

    // Suggest alternative usernames
    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();
        int suffix = 1;
        while (suggestions.size() < 5) {
            String newName = username + suffix;
            if (!usernameMap.containsKey(newName)) {
                suggestions.add(newName);
            }
            suffix++;
        }
        String dotVariation = username.replace("_", ".");
        if (!usernameMap.containsKey(dotVariation)) {
            suggestions.add(dotVariation);
        }
        return suggestions;
    }

    // Get the username with the most attempts
    public String getMostAttempted() {
        return attemptFrequency.entrySet()
                .stream()
                .max(Comparator.comparingInt(e -> e.getValue().get()))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    // Register a new user
    public void registerUsername(String username) {
        if (!usernameMap.containsKey(username)) {
            usernameMap.put(username, userIdCounter++);
            System.out.println("Username '" + username + "' registered successfully!");
        } else {
            System.out.println("Username already taken, cannot register.");
        }
    }

    // User interaction loop
    public void start() {
        while (true) {
            System.out.println("\n--- Social Media Username Checker ---");
            System.out.println("1. Check Availability");
            System.out.println("2. Register Username");
            System.out.println("3. Show Suggestions");
            System.out.println("4. Most Attempted Username");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter username to check: ");
                    String username1 = sc.nextLine();
                    boolean available = checkAvailability(username1);
                    System.out.println("Username '" + username1 + "' available? " + available);
                    break;

                case 2:
                    System.out.print("Enter username to register: ");
                    String username2 = sc.nextLine();
                    registerUsername(username2);
                    break;

                case 3:
                    System.out.print("Enter username for suggestions: ");
                    String username3 = sc.nextLine();
                    List<String> suggestions = suggestAlternatives(username3);
                    System.out.println("Suggestions: " + suggestions);
                    break;

                case 4:
                    String mostAttempted = getMostAttempted();
                    System.out.println("Most attempted username: " + mostAttempted);
                    break;

                case 5:
                    System.out.println("Exiting... Goodbye!");
                    return;

                default:
                    System.out.println("Invalid option, try again.");
            }
        }
    }

    public static void main(String[] args) {
        qn1 checker = new qn1();
        checker.start();
    }
}