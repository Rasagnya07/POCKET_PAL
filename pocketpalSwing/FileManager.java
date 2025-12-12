//FileManager
// CO Mapping: CO1, CO4, CO6
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.time.LocalDate;

public class FileManager {

    private static final String DATA_DIR = "data";
    private static final String EXPENSE_FILE = DATA_DIR + File.separator + "expenses.csv";
    private static final String BUDGET_FILE = DATA_DIR + File.separator + "budgets.csv";
    private static final String STREAK_FILE = DATA_DIR + File.separator + "streak.txt";

    public FileManager() {
        try {
            Path dir = Paths.get(DATA_DIR);
            if (!Files.exists(dir)) Files.createDirectories(dir);
        } catch (IOException e) {
            System.out.println("❌ Could not create data directory: " + DATA_DIR);
            e.printStackTrace();
        }
    }

    public void saveExpenses(List<Expense> expenses) {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(EXPENSE_FILE))) {
            for (Expense e : expenses) {
                bw.write(e.toCSV());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println(ConsoleColors.red("Failed to save: " + EXPENSE_FILE));
            e.printStackTrace();
        }
    }

    public List<Expense> loadExpenses() {
        List<Expense> list = new ArrayList<>();
        Path p = Paths.get(EXPENSE_FILE);
        if (!Files.exists(p)) return list;
        try (BufferedReader br = Files.newBufferedReader(p)) {
            String line;
            while ((line = br.readLine()) != null) {
                Expense e = Expense.fromCSV(line);
                if (e!=null) list.add(e);
            }
        } catch (IOException e) {
            System.out.println(ConsoleColors.red("Failed to load expenses."));
            e.printStackTrace();
        }
        return list;
    }

    public void saveBudget(Budget budget) {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(BUDGET_FILE))) {
            for (String cat : budget.getLimitsCopy().keySet()) {
                double lim = budget.getLimit(cat);
                double sp = budget.getSpent(cat);
                bw.write(cat + "," + lim + "," + sp);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println(ConsoleColors.red("Failed to save: " + BUDGET_FILE));
            e.printStackTrace();
        }
    }

    public Budget loadBudget() {
        Path p = Paths.get(BUDGET_FILE);
        Budget b = new Budget();
        if (!Files.exists(p)) return b;
        List<String> lines = UtilsFileHelpers.readAllLines(BUDGET_FILE);
        b.loadFromLines(lines);
        return b;
    }

    public void saveStreak(LocalDate date, int streak) {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(STREAK_FILE))) {
            bw.write((date == null ? "" : date.toString()) + "," + streak);
        } catch (IOException e) {
            System.out.println(ConsoleColors.red("Failed to save streak."));
            e.printStackTrace();
        }
    }

    public String[] loadStreak() {
        Path p = Paths.get(STREAK_FILE);
        if (!Files.exists(p)) return null;
        try {
            List<String> lines = Files.readAllLines(p);
            if (lines.isEmpty()) return null;
            return lines.get(0).split(",", 2);
        } catch (IOException e) {
            System.out.println(ConsoleColors.red("Failed to load streak."));
            e.printStackTrace();
            return null;
        }
    }
}
