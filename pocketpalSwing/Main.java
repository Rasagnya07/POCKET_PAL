//Main
// CO Mapping: CO1, CO4, CO6
public class Main {
    public static void main(String[] args) {
        FileManager fm = new FileManager();
        Budget budget = fm.loadBudget();
        ExpenseManager em = new ExpenseManager(budget);
        em.loadFromFile(fm);
        BadgeThresholds bth = new BadgeThresholds();
        BadgeManager bm = new BadgeManager(bth);

        double monthlyIncome = 0;
        try {
            String s = Utils.input("Enter monthly pocket money (Rs) (press Enter to skip): ");
            if (!s.isBlank()) monthlyIncome = Double.parseDouble(s);
        } catch (Exception e){ monthlyIncome = 0; }

        while (true) {
            System.out.println(ConsoleColors.bold("\n==== POCKETPAL MENU ===="));
            System.out.println("1. Add expense");
            System.out.println("2. Show expenses");
            System.out.println("3. Budgets");
            System.out.println("4. Badges");
            System.out.println("5. EMI Calculator");
            System.out.println("6. Save & Exit");
            int ch = Utils.inputInt("Choose: ");
            if (ch==1) {
                em.addExpenseInteractive();
                fm.saveExpenses(em.getExpenses());
                fm.saveBudget(budget);
                bm.updateProgressFromExpenseManager(em, budget);
            } else if (ch==2) {
                em.showExpenses();
                Utils.pause();
            } else if (ch==3) {
                BudgetManager bman = new BudgetManager(budget);
                bman.setBudgetLimitInteractive();
                fm.saveBudget(budget);
            } else if (ch==4) {
                bm.updateProgressFromExpenseManager(em, budget);
                bm.showBadges();
                String want = Utils.input("See suggestion for badge (name) or Enter to skip: ");
                if (!want.isBlank()) System.out.println(bm.getSuggestionFor(want, em, budget));
                Utils.pause();
            } else if (ch==5) {
                double p = Utils.inputDouble("Principal (Rs): ");
                double r = Utils.inputDouble("Annual rate (%): ");
                int m = Utils.inputInt("Months: ");
                double emi = EMICalculator.calculateEMI(p,r,m);
                System.out.println(ConsoleColors.green("EMI: Rs"+String.format("%.2f", emi)));
                Utils.pause();
            } else if (ch==6) {
                fm.saveExpenses(em.getExpenses());
                fm.saveBudget(budget);
                System.out.println(ConsoleColors.green("Saved. Bye!"));
                break;
            } else {
                System.out.println(ConsoleColors.red("Invalid choice"));
            }
        }
    }
}
