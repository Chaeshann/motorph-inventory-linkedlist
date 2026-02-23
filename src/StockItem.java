import java.time.LocalDate;

public class StockItem {
    private final LocalDate dateEntered;
    private final String stockLabel;
    private final String brand;
    private final String engineNumber;
    private final String status;

    public StockItem(LocalDate dateEntered, String stockLabel, String brand, String engineNumber, String status) {
        this.dateEntered = dateEntered;
        this.stockLabel = stockLabel;
        this.brand = brand;
        this.engineNumber = engineNumber;
        this.status = status;
    }

    public LocalDate getDateEntered() {
        return dateEntered;
    }

    public String getStockLabel() {
        return stockLabel;
    }

    public String getBrand() {
        return brand;
    }

    public String getEngineNumber() {
        return engineNumber;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return String.format(
            "Date Entered: %s | Label: %s | Brand: %s | Engine #: %s | Status: %s",
            dateEntered, stockLabel, brand, engineNumber, status
        );
    }
}
