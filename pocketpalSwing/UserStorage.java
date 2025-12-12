//UserStorage
// CO Mapping: CO4, CO5, CO6
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

public class UserStorage {
    private static final Path DATA_DIR = Paths.get("data");
    private static final Path USERS_FILE = DATA_DIR.resolve("users.csv");

    public UserStorage() {
        try {
            if (!Files.exists(DATA_DIR)) Files.createDirectories(DATA_DIR);
            if (!Files.exists(USERS_FILE)) Files.write(USERS_FILE, Collections.emptyList());
        } catch (IOException e) { e.printStackTrace(); }
    }

    public Path getUserFile(String username) {
        String safe = username.replaceAll("[^A-Za-z0-9_-]", "_");
        return DATA_DIR.resolve("user_" + safe + ".csv");
    }

    public boolean registerUser(String username, String password, double monthly) {
        try {
            List<String> lines = Files.readAllLines(USERS_FILE);
            for (String L: lines) {
                String[] p = L.split(",",2);
                if (p.length>=1 && p[0].equals(username)) return false;
            }
            Files.write(USERS_FILE, Arrays.asList(username + "," + password), StandardOpenOption.APPEND);
            Path userFile = getUserFile(username);
            List<String> out = new ArrayList<>();
            out.add("PROFILE," + username + "," + monthly);
            out.add("BUDGET,Food,2000,0");
            out.add("BUDGET,Travel,1000,0");
            out.add("BUDGET,Books,500,0");
            out.add("BUDGET,Subscriptions,300,0");
            out.add("BUDGET,Entertainment,1000,0");
            out.add("BUDGET,Shopping,1500,0");
            Files.write(userFile, out);
            return true;
        } catch (IOException e) { e.printStackTrace(); return false; }
    }

    public boolean authenticate(String username, String password) {
        try {
            List<String> lines = Files.readAllLines(USERS_FILE);
            for (String L: lines) {
                String[] p = L.split(",",2);
                if (p.length>=2 && p[0].equals(username) && p[1].equals(password)) return true;
            }
        } catch (IOException e) {}
        return false;
    }

    public UserFileData load(String username) {
        UserFileData out = new UserFileData(username);
        Path p = getUserFile(username);
        if (!Files.exists(p)) return out;
        try (BufferedReader br = Files.newBufferedReader(p)) {
            String line;
            List<String> budgetLines = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(",",2);
                String tag = parts[0];
                String rest = parts.length>1? parts[1] : "";
                switch (tag) {
                    case "PROFILE":
                        String[] prof = rest.split(",",2);
                        if (prof.length>=2) out.monthlyIncome = Double.parseDouble(prof[1]);
                        else out.monthlyIncome = Double.parseDouble(prof[0]);
                        break;
                    case "BUDGET":
                        budgetLines.add(rest);
                        break;
                    case "EXPENSE":
                        Expense e = Expense.fromCSV(rest);
                        if (e!=null) out.expenses.add(e);
                        break;
                    case "BADGE":
                        String[] bd = rest.split(",",3);
                        String name = bd[0];
                        double prog = bd.length>1? Double.parseDouble(bd[1]) : 0.0;
                        Badge b = new Badge(name, "desc");
                        b.setProgress(prog);
                        out.badges.put(name, b);
                        break;
                }
            }
            if (!budgetLines.isEmpty()) out.budget.loadFromLines(budgetLines);
        } catch (IOException e) { e.printStackTrace(); }
        return out;
    }

    public boolean save(String username, double monthlyIncome, Budget budget, List<Expense> expenses, Map<String,Badge> badges) {
        Path p = getUserFile(username);
        try (BufferedWriter bw = Files.newBufferedWriter(p)) {
            bw.write("PROFILE," + username + "," + monthlyIncome);
            bw.newLine();
            for (String L: budget.toLines()) { bw.write("BUDGET,"+L); bw.newLine(); }
            for (Expense e: expenses) { bw.write("EXPENSE," + e.toCSV()); bw.newLine(); }
            if (badges!=null) {
                for (Badge b: badges.values()) {
                    bw.write("BADGE," + b.getName() + "," + b.getProgress() + "," + (b.isUnlocked()? "1":"0"));
                    bw.newLine();
                }
            }
            return true;
        } catch (IOException ex) { ex.printStackTrace(); return false; }
    }

    public static class UserFileData {
        public final String username;
        public double monthlyIncome = 0.0;
        public Budget budget = new Budget();
        public List<Expense> expenses = new ArrayList<>();
        public Map<String,Badge> badges = new LinkedHashMap<>();
        public UserFileData(String u) { username = u; }
    }
}
