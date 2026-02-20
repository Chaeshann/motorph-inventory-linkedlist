import java.util.LinkedList;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

public class Inventory {

    private LinkedList<StockItem> stocks;

    public Inventory() {
        stocks = new LinkedList<>();
    }

    // ENUM for single search/delete criterion
    public enum CriteriaType {
        ENGINE_NUMBER,
        BRAND,
        STATUS,
        STOCK_LABEL,
        DATE_ENTERED
    }

    // ADD STOCK
    public void addStock(StockItem item) {
        stocks.add(item);
    }

    // SIZE
    public int size() {
        return stocks.size();
    }

    // GET ALL
    public List<StockItem> getAll() {
        return new LinkedList<>(stocks);
    }

    // SEARCH (Single Criterion)
    public List<StockItem> search(CriteriaType type, String value) {

        return stocks.stream()
                .filter(item -> matches(item, type, value))
                .collect(Collectors.toList());
    }

    // DELETE FIRST MATCH
    public boolean deleteFirstMatch(CriteriaType type, String value) {
        for (StockItem item : stocks) {
            if (matches(item, type, value)) {
                stocks.remove(item);
                return true;
            }
        }
        return false;
    }

    // DELETE ALL MATCHES
    public int deleteAllMatches(CriteriaType type, String value) {
        int before = stocks.size();
        stocks.removeIf(item -> matches(item, type, value));
        return before - stocks.size();
    }

    // SORT BY BRAND (A-Z)
    public void sortByBrandAscending() {
        stocks.sort(Comparator.comparing(StockItem::getBrand, String.CASE_INSENSITIVE_ORDER));
    }

    // MATCHING LOGIC
    private boolean matches(StockItem item, CriteriaType type, String value) {

        switch (type) {
            case ENGINE_NUMBER:
                return item.getEngineNumber().equalsIgnoreCase(value);

            case BRAND:
                return item.getBrand().equalsIgnoreCase(value);

            case STATUS:
                return item.getStatus().equalsIgnoreCase(value);

            case STOCK_LABEL:
                return item.getStockLabel().equalsIgnoreCase(value);

            case DATE_ENTERED:
                return item.getDateEntered().toString().equalsIgnoreCase(value);

            default:
                return false;
        }
    }
}

