//BadgeManager
// CO Mapping: CO3, CO4, CO5, CO6
import java.util.*;

public class BadgeManager {
    private final Map<String,Badge> badges = new LinkedHashMap<>();
    private final BadgeThresholds thresholds;

    public BadgeManager(BadgeThresholds thresholds){
        this.thresholds = thresholds;
        for (String k: thresholds.badges()) badges.put(k, new Badge(k, k+" badge"));
    }

    public void updateProgressFromExpenseManager(ExpenseManager em, Budget budget) {
        double saved = 0;
        for (String cat: budget.getCategories()) {
            double lim = budget.getLimit(cat);
            double sp = budget.getSpent(cat);
            if (lim>0 && sp < lim) saved += (lim - sp);
        }
        int count = em.getExpenseCount();
        for (String key: badges.keySet()) {
            double thr = thresholds.get(key);
            double pct = 0;
            switch (key) {
                case "Future Funded": pct = Math.min(100.0, (saved / thr) * 100.0); break;
                case "Money Magnet": pct = Math.min(100.0, (em.getStreak() / thr) * 100.0); break;
                case "Cash Stash": pct = Math.min(100.0, (saved / thr) * 100.0); break;
                case "Penny Pioneer": pct = Math.min(100.0, (saved / thr) * 100.0); break;
                case "Expense Expert": pct = Math.min(100.0, (count / thr) * 100.0); break;
                case "Trend Tracker": pct = Math.min(100.0, (count / thr) * 100.0); break;
                case "Budget Breeze":
                    int ok=0, total=0;
                    for (String c: budget.getCategories()) { total++; if (budget.getSpent(c) <= 0.8*budget.getLimit(c)) ok++; }
                    pct = total==0?0: (ok/(double)total)*100.0;
                    break;
                case "Habit Hero": pct = Math.min(100.0, (em.getStreak() / thr) * 100.0); break;
                default: pct = 0;
            }
            badges.get(key).setProgress(pct);
        }
    }

    public void showBadges() {
        System.out.println(ConsoleColors.purple("\n--- Badges ---"));
        for (Badge b: badges.values()) {
            String line = b.getName() + " : " + Utils.progressBar(b.getProgress()) + (b.isUnlocked() ? " 🔓" : "");
            System.out.println(line);
        }
    }

    public String getSuggestionFor(String badgeName, ExpenseManager em, Budget budget) {
        Badge b = badges.get(badgeName);
        if (b==null) return "No such badge.";
        double p = b.getProgress();
        double remain = 100.0 - p;
        switch (badgeName) {
            case "Cash Stash":
            case "Future Funded":
                return "Suggestion: Reduce wants by Rs500/month to accelerate — estimated months: " + (int)Math.ceil((remain/100.0)* (thresholds.get(badgeName)/500.0));
            case "Expense Expert":
                return "Suggestion: Log expenses consistently — add "+ (int)(thresholds.get(badgeName)-em.getExpenseCount()) +" more entries.";
            case "Budget Breeze":
                return "Suggestion: Trim top spending categories by 10% this month.";
            default:
                return "Keep using the app; progress will update as you add expenses.";
        }
    }

    public Set<String> badgeNames(){ return badges.keySet(); }
    public Badge getBadge(String name){ return badges.get(name); }
    public Map<String,Badge> getAllBadges(){ return badges; }
}
