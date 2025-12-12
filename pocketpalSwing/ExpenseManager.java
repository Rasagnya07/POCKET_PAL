//ExpenseManager
// CO Mapping: CO1, CO3, CO4, CO6
import java.time.LocalDate;
import java.util.*;

public class ExpenseManager {
    private final Budget budget;
    private final List<Expense> expenses = new ArrayList<>();
    private final NeedsWantsClassifier classifier = new NeedsWantsClassifier();
    private LocalDate lastActionDate = null;
    private int streak = 0;

    public ExpenseManager(Budget budget) {
        this.budget = budget==null? new Budget() : budget;
    }

    public void loadFromFile(FileManager fm) {
        List<Expense> loaded = fm.loadExpenses();
        if (loaded!=null) {
            expenses.clear();
            expenses.addAll(loaded);
            for (Expense e: expenses) budget.addSpent(e.getCategory(), e.getAmount());
        }
        String[] s = fm.loadStreak();
        if (s!=null && s.length>=2) {
            try {
                lastActionDate = LocalDate.parse(s[0]);
                streak = Integer.parseInt(s[1]);
            } catch (Exception e) {}
        }
    }

    public void addExpenseInteractive() {
        String cat = Utils.input("Category: ");
        double amt = Utils.inputDouble("Amount (Rs): ");
        String type = Utils.input("Type (Need/Want) [enter for auto]: ");
        if (type.isBlank()) type = classifier.classify(cat);
        String note = Utils.input("Note (optional): ");
        Expense e = new Expense(LocalDate.now(), cat, amt, type, note);
        expenses.add(e);
        budget.addSpent(cat, amt);
        updateStreak();
        System.out.println(ConsoleColors.green("Added: "+e));
    }

    public void addExpense(String category, double amount, String type, String note) {
        Expense e = new Expense(LocalDate.now(), category, amount, type, note);
        expenses.add(e);
        budget.addSpent(category, amount);
        updateStreak();
    }

    private void updateStreak(){
        LocalDate today = LocalDate.now();
        if (lastActionDate==null) streak=1;
        else {
            if (lastActionDate.plusDays(1).equals(today)) streak++;
            else if (!lastActionDate.equals(today)) streak=1;
        }
        lastActionDate = today;
    }

    public List<Expense> getExpenses(){ return new ArrayList<>(expenses); }
    public int getExpenseCount(){ return expenses.size(); }
    public double getTotalSpent(){ return budget.getTotalSpent(); }
    public int getStreak(){ return streak; }

    public void showExpenses() {
        System.out.println(ConsoleColors.blue("\n--- Expenses ---"));
        if (expenses.isEmpty()) { System.out.println(ConsoleColors.blue("No expenses yet.")); return; }
        for (Expense e: expenses) System.out.println(e);
    }

    public Map<String, Double> getCategoryTotals(){
        Map<String, Double> m = new LinkedHashMap<>();
        for (Expense e: expenses) m.put(e.getCategory(), m.getOrDefault(e.getCategory(),0.0)+e.getAmount());
        for (String cat: budget.getCategories()) m.putIfAbsent(cat, budget.getSpent(cat));
        return m;
    }

    public Map<String, Double> getNeedsWantsBreakdown(){
        double needs=0, wants=0;
        for (Expense e: expenses) {
            if ("Need".equalsIgnoreCase(e.getType())) needs+=e.getAmount(); else wants+=e.getAmount();
        }
        Map<String, Double> out = new HashMap<>();
        out.put("needs", needs); out.put("wants", wants);
        return out;
    }
}
