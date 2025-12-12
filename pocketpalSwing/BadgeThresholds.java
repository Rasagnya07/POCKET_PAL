//BadgeThresholds
// CO Mapping: CO1, CO2, CO5
import java.util.*;

public class BadgeThresholds {
    private final Map<String, Double> thresholds = new LinkedHashMap<>();
    public BadgeThresholds(){
        thresholds.put("Future Funded", 5000.0);
        thresholds.put("Money Magnet", 7.0);
        thresholds.put("Cash Stash", 10000.0);
        thresholds.put("Penny Pioneer", 500.0);
        thresholds.put("Expense Expert", 20.0);
        thresholds.put("Budget Breeze", 80.0);
        thresholds.put("Habit Hero", 21.0);
        thresholds.put("Trend Tracker", 10.0);
    }
    public double get(String badge){ return thresholds.getOrDefault(badge, 100.0); }
    public Set<String> badges(){ return thresholds.keySet(); }
}
