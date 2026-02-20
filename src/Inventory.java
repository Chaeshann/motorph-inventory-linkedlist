import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Inventory {

    public enum CriteriaType {
        ENGINE_NUMBER,
        BRAND,
        STATUS,
        STOCK_LABEL,
        DATE_ENTERED
    }

    private final LinkedList<StockItem> inventoryList;

    public Inventory() {
        this.inventoryList = new LinkedList<>();
    }

    public int size() {
        return inventoryList.size();
    }

    public void addStock(StockItem item) {
        inventoryList.addLast(item);
    }

    public List<StockItem> search(CriteriaType type, String value) {
        LinkedList<StockItem> results = new LinkedList<>();

        for (StockItem item : inventoryList) {
            if (matches(item, type, value)) {
                results.add(item);
            }
        }

        return results;
    }

    public boolean deleteFirstMatch(CriteriaType type, String value) {
        Iterator<StockItem> it = inventoryList.iterator();

        while (it.hasNext()) {
            StockItem item = it.next();
            if (matches(item, type, value)) {
                it.remove();
                return true;
            }
        }

        return false;
    }

    public int deleteAllMatches(CriteriaType type, String value) {
        int count = 0;
        Iterator<StockItem> it = inventoryList.iterator();

        while (it.hasNext()) {
            StockItem item = it.next();
            if (matches(item, type, value)) {
                it.remove();
                count++;
            }
        }

        return count;
    }

    public void sortByBrandAscending() {
        Collections.sort(inventoryList,
                Comparator.comparing((StockItem s) -> safeLower(s.getBrand()))
                          .thenComparing(s -> safeLower(s.getEngineNumber()))
        );
    }

    public List<StockItem> getAll() {
        return new LinkedList<>(inventoryList);
    }

    private boolean matches(StockItem item, CriteriaType type, String value) {
        String v = value == null ? "" : value.trim();

        switch (type) {
            case ENGINE_NUMBER:
                return safeLower(item.getEngineNumber()).equals(safeLower(v));

            case BRAND:
                return safeLower(item.getBrand()).contains(safeLower(v));

            case STATUS:
                return safeLower(item.getStatus()).equals(safeLower(v));

            case STOCK_LABEL:
                return safeLower(item.getStockLabel()).equals(safeLower(v));

            case DATE_ENTERED:
                try {
                    LocalDate d = LocalDate.parse(v);
                    return item.getDateEntered().equals(d);
                } catch (Exception e) {
                    return false;
                }

            default:
                return false;
        }
    }

    private String safeLower(String s) {
        return (s == null) ? "" : s.trim().toLowerCase();
    }
}
