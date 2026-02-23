import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class MotorPHInventoryApp {

    private static final String CSV_PATH = "resources/MotorPH_Inventory.csv";

    public static void main(String[] args) {
        Inventory inventory = new Inventory();
        Scanner sc = new Scanner(System.in);

        int loaded = tryLoadFromCsv(inventory, CSV_PATH);
        if (loaded > 0) {
            System.out.println("Loaded " + loaded + " records from " + CSV_PATH);
        } else {
            System.out.println("No CSV loaded. You can still add stocks manually.");
        }

        while (true) {
            printMenu();
            System.out.print("Choose an option: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    addStockFlow(inventory, sc);
                    break;
                case "2":
                    searchFlow(inventory, sc);
                    break;
                case "3":
                    deleteFlow(inventory, sc);
                    break;
                case "4":
                    inventory.sortByBrandAscending();
                    System.out.println("Inventory sorted by Brand (A–Z).");
                    break;
                case "5":
                    displayAll(inventory);
                    break;
                case "0":
                    System.out.println("Exiting... Goodbye!");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n=== MotorPH Inventory Management (Phase 3) ===");
        System.out.println("[1] Add Stock");
        System.out.println("[2] Search Stock (Single Criterion)");
        System.out.println("[3] Delete Stock (Single Criterion)");
        System.out.println("[4] Sort by Brand (A–Z)");
        System.out.println("[5] Display All Inventory");
        System.out.println("[0] Exit");
    }

    private static void addStockFlow(Inventory inventory, Scanner sc) {
        System.out.println("\n--- Add Stock ---");

        LocalDate dateEntered = askDate(sc, "Date Entered (YYYY-MM-DD): ");

        System.out.print("Stock Label (Old/New): ");
        String label = sc.nextLine().trim();

        System.out.print("Brand: ");
        String brand = sc.nextLine().trim();

        System.out.print("Engine Number: ");
        String engineNo = sc.nextLine().trim();

        System.out.print("Status (On-hand/Sold): ");
        String status = sc.nextLine().trim();

        StockItem item = new StockItem(dateEntered, label, brand, engineNo, status);
        inventory.addStock(item);

        System.out.println("Added: " + item);
        System.out.println("Total records: " + inventory.size());
    }

    private static void searchFlow(Inventory inventory, Scanner sc) {
        System.out.println("\n--- Search Stock (Single Criterion) ---");

        Inventory.CriteriaType type = askCriteria(sc);

        System.out.print("Enter value: ");
        String value = sc.nextLine().trim();

        List<StockItem> results = inventory.search(type, value);

        if (results.isEmpty()) {
            System.out.println("No matching stock found.");
            return;
        }

        System.out.println("Found " + results.size() + " match(es):");
        for (StockItem item : results) {
            System.out.println(" - " + item);
        }
    }

    private static void deleteFlow(Inventory inventory, Scanner sc) {
        System.out.println("\n--- Delete Stock (Single Criterion) ---");

        Inventory.CriteriaType type = askCriteria(sc);

        System.out.print("Enter value: ");
        String value = sc.nextLine().trim();

        System.out.print("Delete FIRST match only? (Y/N): ");
        String yn = sc.nextLine().trim().toLowerCase();

        if (yn.equals("n")) {
            int count = inventory.deleteAllMatches(type, value);
            System.out.println("Deleted " + count + " record(s).");
        } else {
            boolean deleted = inventory.deleteFirstMatch(type, value);
            System.out.println(deleted ? "Deleted 1 record." : "No matching stock found.");
        }

        System.out.println("Total records: " + inventory.size());
    }

    private static void displayAll(Inventory inventory) {
        System.out.println("\n--- Inventory Records (" + inventory.size() + ") ---");
        List<StockItem> all = inventory.getAll();

        if (all.isEmpty()) {
            System.out.println("(empty)");
            return;
        }

        for (StockItem item : all) {
            System.out.println(" - " + item);
        }
    }

    private static Inventory.CriteriaType askCriteria(Scanner sc) {
        while (true) {
            System.out.println("Choose criterion:");
            System.out.println(" [1] Engine Number");
            System.out.println(" [2] Brand");
            System.out.println(" [3] Status");
            System.out.println(" [4] Stock Label");
            System.out.println(" [5] Date Entered");
            System.out.print("Enter choice: ");

            String c = sc.nextLine().trim();

            switch (c) {
                case "1": return Inventory.CriteriaType.ENGINE_NUMBER;
                case "2": return Inventory.CriteriaType.BRAND;
                case "3": return Inventory.CriteriaType.STATUS;
                case "4": return Inventory.CriteriaType.STOCK_LABEL;
                case "5": return Inventory.CriteriaType.DATE_ENTERED;
                default:
                    System.out.println("Invalid. Try again.");
            }
        }
    }

    private static LocalDate askDate(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();

            try {
                return LocalDate.parse(s);
            } catch (Exception e) {
                System.out.println("Invalid date format. Use YYYY-MM-DD.");
            }
        }
    }

    private static int tryLoadFromCsv(Inventory inventory, String path) {
        int count = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = br.readLine(); // header
            if (line == null) return 0;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 5) continue;

                LocalDate date = parseDateLenient(parts[0].trim());
                String label = parts[1].trim();
                String brand = parts[2].trim();
                String engine = parts[3].trim();
                String status = parts[4].trim();

                if (date == null) continue;

                inventory.addStock(new StockItem(date, label, brand, engine, status));
                count++;
            }
        } catch (Exception e) {
            return 0;
        }

        return count;
    }

    private static LocalDate parseDateLenient(String raw) {
        if (raw == null) return null;
        String s = raw.trim();
        if (s.isEmpty()) return null;

        try { return LocalDate.parse(s); } catch (Exception ignored) {}

        try {
            DateTimeFormatter f = DateTimeFormatter.ofPattern("M/d/yyyy");
            return LocalDate.parse(s, f);
        } catch (Exception ignored) {}

        try {
            DateTimeFormatter f = DateTimeFormatter.ofPattern("d/M/yyyy");
            return LocalDate.parse(s, f);
        } catch (Exception ignored) {}

        return null;
    }
}
