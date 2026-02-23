private static final String CSV_PATH = "resources/MotorPH_Inventory.csv";

void main() {
    Inventory inventory = new Inventory();
    Scanner sc = new Scanner(System.in);

    int loaded = tryLoadFromCsv(inventory, CSV_PATH);
    if (loaded > 0) {
        IO.println("Loaded " + loaded + " records from " + CSV_PATH);
    } else {
        IO.println("No CSV loaded. You can still add stocks manually.");
    }

    while (true) {
        printMenu();
        IO.print("Choose an option: ");
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
                IO.println("Inventory sorted by Brand (A–Z).");
                break;
            case "5":
                displayAll(inventory);
                break;
            case "0":
                IO.println("Exiting... Goodbye!");
                sc.close();
                return;
            default:
                IO.println("Invalid choice. Try again.");
        }
    }
}

private static void printMenu() {
    IO.println("\n=== MotorPH Inventory Management (Phase 3) ===");
    IO.println("[1] Add Stock");
    IO.println("[2] Search Stock (Single Criterion)");
    IO.println("[3] Delete Stock (Single Criterion)");
    IO.println("[4] Sort by Brand (A–Z)");
    IO.println("[5] Display All Inventory");
    IO.println("[0] Exit");
}

private static void addStockFlow(Inventory inventory, Scanner sc) {
    IO.println("\n--- Add Stock ---");

    LocalDate dateEntered = readDate (sc);

    IO.print("Stock Label (Old/New): ");
    String label = sc.nextLine().trim();

    IO.print("Brand: ");
    String brand = sc.nextLine().trim();

    IO.print("Engine Number: ");
    String engineNo = sc.nextLine().trim();

    IO.print("Status (On-hand/Sold): ");
    String status = sc.nextLine().trim();

    StockItem item = new StockItem(dateEntered, label, brand, engineNo, status);
    inventory.addStock(item);

    IO.println("Added: " + item);
    IO.println("Total records: " + inventory.size());
}

private static void searchFlow(Inventory inventory, Scanner sc) {
    IO.println("\n--- Search Stock (Single Criterion) ---");

    Inventory.CriteriaType type = askCriteria(sc);

    IO.print("Enter value: ");
    String value = sc.nextLine().trim();

    List<StockItem> results = inventory.search(type, value);

    if (results.isEmpty()) {
        IO.println("No matching stock found.");
        return;
    }

    IO.println("Found " + results.size() + " match(es):");
    for (StockItem item : results) {
        IO.println(" - " + item);
    }
}

private static void deleteFlow(Inventory inventory, Scanner sc) {
    IO.println("\n--- Delete Stock (Single Criterion) ---");

    Inventory.CriteriaType type = askCriteria(sc);

    IO.print("Enter value: ");
    String value = sc.nextLine().trim();

    IO.print("Delete FIRST match only? (Y/N): ");
    String yn = sc.nextLine().trim().toLowerCase();

    if (yn.equals("n")) {
        int count = inventory.deleteAllMatches(type, value);
        IO.println("Deleted " + count + " record(s).");
    } else {
        boolean deleted = inventory.deleteFirstMatch(type, value);
        IO.println(deleted ? "Deleted 1 record." : "No matching stock found.");
    }

    IO.println("Total records: " + inventory.size());
}

private static void displayAll(Inventory inventory) {
    IO.println("\n--- Inventory Records (" + inventory.size() + ") ---");
    List<StockItem> all = inventory.getAll();

    if (all.isEmpty()) {
        IO.println("(empty)");
        return;
    }

    for (StockItem item : all) {
        IO.println(" - " + item);
    }
}

private static Inventory.CriteriaType askCriteria(Scanner sc) {
    while (true) {
        IO.println("Choose criterion:");
        IO.println(" [1] Engine Number");
        IO.println(" [2] Brand");
        IO.println(" [3] Status");
        IO.println(" [4] Stock Label");
        IO.println(" [5] Date Entered");
        IO.print("Enter choice: ");

        String c = sc.nextLine().trim();

        switch (c) {
            case "1":
                return Inventory.CriteriaType.ENGINE_NUMBER;
            case "2":
                return Inventory.CriteriaType.BRAND;
            case "3":
                return Inventory.CriteriaType.STATUS;
            case "4":
                return Inventory.CriteriaType.STOCK_LABEL;
            case "5":
                return Inventory.CriteriaType.DATE_ENTERED;
            default:
                IO.println("Invalid. Try again.");
        }
    }
}

private static LocalDate readDate(Scanner sc) {
    while (true) {
        IO.print("Date Entered (YYYY-MM-DD): ");
        String s = sc.nextLine().trim();

        try {
            return LocalDate.parse(s);
        } catch (Exception e) {
            IO.println("Invalid date format. Use YYYY-MM-DD.");
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

    try {
        return LocalDate.parse(s);
    } catch (Exception ignored) {
    }

    try {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("M/d/yyyy");
        return LocalDate.parse(s, f);
    } catch (Exception ignored) {
    }

    try {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("d/M/yyyy");
        return LocalDate.parse(s, f);
    } catch (Exception ignored) {
    }

    return null;
}
