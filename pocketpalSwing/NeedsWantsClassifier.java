//NeedsWantsClassifier 
// CO Mapping: CO1, CO3
import java.util.*;

/**
 * Simple rule-based classifier to auto-classify categories into "Need" or "Want".
 * Keeps a small default map and falls back to keyword checks.
 */
public class NeedsWantsClassifier {
    private final Map<String,String> defaults = new LinkedHashMap<>();

    public NeedsWantsClassifier() {
        // sensible defaults (lowercase keys)
        defaults.put("food","Need");
        defaults.put("groceries","Need");
        defaults.put("grocery","Need");
        defaults.put("rent","Need");
        defaults.put("travel","Need");
        defaults.put("bus","Need");
        defaults.put("train","Need");
        defaults.put("books","Need");
        defaults.put("tuition","Need");
        defaults.put("med","Need");
        defaults.put("pharmacy","Need");

        defaults.put("subscriptions","Want");
        defaults.put("entertainment","Want");
        defaults.put("shopping","Want");
        defaults.put("netflix","Want");
        defaults.put("movie","Want");
        defaults.put("spa","Want");
        defaults.put("cafe","Want");
        defaults.put("party","Want");
    }

    /**
     * Classify a category string as "Need" or "Want".
     * If exact match in defaults -> return that.
     * Else uses keyword contains checks.
     */
    public String classify(String category) {
        if (category == null) return "Want";
        String k = category.trim().toLowerCase();
        if (k.isEmpty()) return "Want";

        // exact default match
        if (defaults.containsKey(k)) return defaults.get(k);

        // keyword checks for Needs
        String[] needKeys = {"rent","bill","tuition","grocer","groc","fuel","repair","med","pharm","bus","train","electric","water"};
        for (String kw: needKeys) if (k.contains(kw)) return "Need";

        // keyword checks for Wants
        String[] wantKeys = {"shop","spa","cinema","movie","game","dress","party","cafe","netflix","subscription","uber","swiggy","zomato"};
        for (String kw: wantKeys) if (k.contains(kw)) return "Want";

        // fallback: if category looks like essentials (short heuristics)
        if (k.length() <= 4 && (k.equals("food") || k.equals("milk") || k.equals("veg"))) return "Need";

        return "Want";
    }

    /** Allow user to set custom default mapping (category -> Need/Want) */
    public void setDefault(String category, String type) {
        if (category == null || type == null) return;
        String t = type.trim().equalsIgnoreCase("Need") ? "Need" : "Want";
        defaults.put(category.trim().toLowerCase(), t);
    }

    /** Return a copy of defaults for display or persistence */
    public Map<String,String> getDefaultsCopy() {
        return new LinkedHashMap<>(defaults);
    }
}
