//BudgetManager
// CO Mapping: CO4, CO6
public class BudgetManager {
    private final Budget budget;
    public BudgetManager(Budget b){ this.budget = b; }

    public void setBudgetLimitInteractive() {
        String cat = Utils.input("Category name: ");
        double lim = Utils.inputDouble("Monthly limit (Rs): ");
        budget.setLimit(cat, lim);
        System.out.println(ConsoleColors.green("Budget set: "+cat+" -> Rs"+lim));
    }

    public Budget getBudget(){ return budget; }

    public boolean isOverspent(String category) {
        double lim = budget.getLimit(category);
        if (lim<=0) return false;
        return budget.getSpent(category) > lim;
    }
}
