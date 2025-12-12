//Budget
// CO Mapping: CO1, CO2, CO4, CO6
import java.util.*;

public class Budget {
    private final Map<String, Double> limits = new LinkedHashMap<>();
    private final Map<String, Double> spent = new LinkedHashMap<>();

    public Budget() {
        // sensible defaults
        setLimit("Food", 2000);
        setLimit("Travel", 1000);
        setLimit("Books", 500);
        setLimit("Subscriptions", 300);
        setLimit("Entertainment", 1000);
        setLimit("Shopping", 1500);
    }

    public void setLimit(String category, double limit) {
        category = category==null? "Other": category.trim();
        limits.put(category, limit);
        spent.putIfAbsent(category, 0.0);
    }

    public double getLimit(String category) { return limits.getOrDefault(category, 0.0); }
    public double getSpent(String category) { return spent.getOrDefault(category, 0.0); }

    public void addSpent(String category, double amount) {
        category = category==null? "Other": category.trim();
        spent.put(category, getSpent(category)+amount);
        limits.putIfAbsent(category, 0.0);
    }

    public Set<String> getCategories(){ return limits.keySet(); }

    public double getTotalSpent() {
        double s=0; for (double v: spent.values()) s+=v; return s;
    }

    public Map<String, Double> getLimitsCopy(){ return new LinkedHashMap<>(limits); }
    public Map<String, Double> getSpentCopy(){ return new LinkedHashMap<>(spent); }

    public List<String> toLines() {
        List<String> out = new ArrayList<>();
        for (String cat: limits.keySet()) {
            out.add(cat + "," + limits.get(cat) + "," + getSpent(cat));
        }
        return out;
    }

    public void loadFromLines(List<String> lines) {
        for (String L: lines) {
            try {
                String[] p = L.split(",",3);
                String cat = p[0];
                double lim = Double.parseDouble(p[1]);
                double sp = Double.parseDouble(p[2]);
                setLimit(cat, lim);
                spent.put(cat, sp);
            } catch (Exception e) {}
        }
    }
}
