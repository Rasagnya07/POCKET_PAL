// PocketPalSwing.java
// Full Swing UI for PocketPal (login -> dashboard -> add expense -> reports -> badges -> EMI)
// CO Mapping: CO1, CO3, CO4, CO5, CO6

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.*;
import java.io.*;

/*
 Depends on these files (must be present in same folder):
 - UserStorage.java
 - Expense.java
 - Budget.java
 - NeedsWantsClassifier.java
 - BadgeManager.java
 - BadgeThresholds.java
 - Badge.java
 - ExpenseManager.java
 - ConsoleColors.java
 - EMICalculator.java (optional — if present, used)
 - FileManager.java
 - Utils.java
*/

public class PocketPalSwing {
    private final UserStorage storage = new UserStorage();
    private String currentUser;
    private UserStorage.UserFileData userData;
    private BadgeManager badgeManager;
    private final BadgeThresholds bth = new BadgeThresholds();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PocketPalSwing().showLogin());
    }

    // ------------------- Login / Signup -------------------
    void showLogin() {
        JFrame loginFrame = new JFrame("PocketPal - Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(480, 340);
        loginFrame.setLocationRelativeTo(null);

        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        root.setBackground(new Color(173, 233, 227));

        JLabel title = new JLabel("PocketPal");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        root.add(title);
        root.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.setBackground(new Color(173, 233, 227));
        JTextField tfUser = new JTextField();
        JPasswordField pf = new JPasswordField();
        JTextField tfMonthly = new JTextField();

        form.add(new JLabel("Username:")); form.add(tfUser);
        form.add(new JLabel("Password:")); form.add(pf);
        form.add(new JLabel("Monthly (optional):")); form.add(tfMonthly);
        root.add(form);

        root.add(Box.createRigidArea(new Dimension(0, 12)));
        JPanel btns = new JPanel();
        btns.setBackground(new Color(173, 233, 227));
        JButton login = new JButton("Login"); styleBtn(login);
        JButton signup = new JButton("Sign up"); styleBtn(signup);
        btns.add(login); btns.add(signup);
        root.add(btns);

        loginFrame.add(root);
        loginFrame.setVisible(true);

        login.addActionListener(e -> {
            String u = tfUser.getText().trim();
            String p = new String(pf.getPassword());
            if (u.isEmpty() || p.isEmpty()) { JOptionPane.showMessageDialog(loginFrame, "Enter username & password"); return; }
            if (!storage.authenticate(u, p)) { JOptionPane.showMessageDialog(loginFrame, "Invalid username/password"); return; }
            currentUser = u;
            userData = storage.load(u);
            badgeManager = new BadgeManager(bth);
            loginFrame.dispose();
            showDashboard();
        });

        signup.addActionListener(e -> {
            String u = tfUser.getText().trim();
            String p = new String(pf.getPassword());
            double m = 0;
            try { if (!tfMonthly.getText().trim().isEmpty()) m = Double.parseDouble(tfMonthly.getText().trim()); } catch (Exception ex) {}
            if (u.isEmpty() || p.isEmpty()) { JOptionPane.showMessageDialog(loginFrame, "Enter username & password"); return; }
            if (!storage.registerUser(u, p, m)) { JOptionPane.showMessageDialog(loginFrame, "Username exists"); return; }
            JOptionPane.showMessageDialog(loginFrame, "Registered. Please login.");
        });
    }

    // ------------------- Utilities -------------------
    private void styleBtn(JButton b) {
        b.setBackground(new Color(88, 174, 167));
        b.setForeground(Color.white);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
    }

    /** Try to return a visible dashboard frame, or null. */
    private Frame getActiveDashboardFrame() {
        for (Frame f : Frame.getFrames()) {
            if (f.isVisible() && f.getTitle()!=null && f.getTitle().startsWith("PocketPal - Dashboard (")) return f;
        }
        return null;
    }

    // ------------------- Dashboard (with alert) -------------------
    void showDashboard() {
        userData = storage.load(currentUser); // reload to ensure fresh data
        if (badgeManager == null) badgeManager = new BadgeManager(bth);
        updateBadgeProgress();

        JFrame dash = new JFrame("PocketPal - Dashboard (" + currentUser + ")");
        dash.setSize(920, 560);
        dash.setLocationRelativeTo(null);
        dash.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        root.setBackground(new Color(173, 233, 227));

        JLabel hi = new JLabel("Hi, " + currentUser);
        hi.setFont(new Font("SansSerif", Font.BOLD, 22));
        root.add(hi, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 2, 12, 12));
        center.setBackground(new Color(173, 233, 227));

        // left stats + actions
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(new Color(173, 233, 227));
        left.setBorder(BorderFactory.createLineBorder(new Color(129, 199, 197), 2));

        double total = userData.expenses.stream().mapToDouble(Expense::getAmount).sum();
        JLabel monthlyL = new JLabel("Monthly: Rs " + String.format("%.2f", userData.monthlyIncome));
        JLabel spentL = new JLabel("Total Spent: Rs " + String.format("%.2f", total));
        JLabel remL = new JLabel("Remaining: Rs " + String.format("%.2f", Math.max(0.0, userData.monthlyIncome - total)));
        monthlyL.setBorder(new EmptyBorder(8, 8, 8, 8)); spentL.setBorder(new EmptyBorder(8, 8, 8, 8)); remL.setBorder(new EmptyBorder(8, 8, 8, 8));
        left.add(monthlyL); left.add(spentL); left.add(remL);
        left.add(Box.createRigidArea(new Dimension(0, 12)));

        JButton add = new JButton("Add Expense"); styleBtn(add); add.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton viewBtn = new JButton("View Expenses"); styleBtn(viewBtn);
        JButton emiBtn = new JButton("EMI Calculator"); styleBtn(emiBtn);

        left.add(add); left.add(Box.createRigidArea(new Dimension(0, 8))); left.add(viewBtn); left.add(Box.createRigidArea(new Dimension(0, 8))); left.add(emiBtn);

        center.add(left);

        // right menu
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBackground(new Color(173, 233, 227));
        JButton budgetsBtn = new JButton("Budgets"); styleBtn(budgetsBtn);
        JButton badgesBtn = new JButton("Badges"); styleBtn(badgesBtn);
        JButton reportsBtn = new JButton("Reports"); styleBtn(reportsBtn);
        JButton saveBtn = new JButton("Save"); styleBtn(saveBtn);
        right.add(budgetsBtn); right.add(Box.createRigidArea(new Dimension(0, 8)));
        right.add(badgesBtn); right.add(Box.createRigidArea(new Dimension(0, 8)));
        right.add(reportsBtn); right.add(Box.createRigidArea(new Dimension(0, 8)));
        right.add(saveBtn);

        center.add(right);
        root.add(center, BorderLayout.CENTER);

        // alert panel
        JPanel alertPanel = new JPanel(new BorderLayout());
        alertPanel.setBackground(new Color(255, 235, 238));
        alertPanel.setBorder(BorderFactory.createLineBorder(new Color(244, 67, 54), 2));
        alertPanel.setPreferredSize(new Dimension(0, 60));
        JLabel alertLabel = new JLabel("");
        alertLabel.setForeground(new Color(183, 28, 28));
        alertLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        alertLabel.setHorizontalAlignment(SwingConstants.CENTER);
        alertPanel.add(alertLabel, BorderLayout.CENTER);
        alertPanel.setVisible(false);

        // compute overspent categories
        StringBuilder alerts = new StringBuilder();
        for (String cat : userData.budget.getLimitsCopy().keySet()) {
            double lim = userData.budget.getLimit(cat);
            double sp = userData.budget.getSpent(cat);
            if (lim > 0 && sp > lim) {
                alerts.append(cat).append(": over by Rs ").append(String.format("%.2f", sp - lim)).append("   ");
            }
        }
        if (alerts.length() > 0) {
            alertLabel.setText("⚠ Overspent: " + alerts.toString());
            alertPanel.setVisible(true);
        }

        root.add(alertPanel, BorderLayout.SOUTH);

        dash.add(root);
        dash.setVisible(true);

        // actions
        add.addActionListener(e -> showAddExpenseDialog(dash));
        viewBtn.addActionListener(e -> showExpensesDialog(dash));
        emiBtn.addActionListener(e -> showEmiDialog(dash));
        budgetsBtn.addActionListener(e -> showBudgetsDialog(dash));
        badgesBtn.addActionListener(e -> showBadgesDialog(dash));
        reportsBtn.addActionListener(e -> showReportDialog(dash));
        saveBtn.addActionListener(e -> {
            boolean ok = storage.save(userData.username, userData.monthlyIncome, userData.budget, userData.expenses, userData.badges);
            JOptionPane.showMessageDialog(dash, ok ? "Saved" : "Save failed (check console)");
        });
    }

    // ------------------- Add Expense (with overspend pop-up & badge penalty) -------------------
    void showAddExpenseDialog(JFrame parent) {
        JDialog d = new JDialog(parent, "Add Expense", true);
        d.setSize(480, 360);
        d.setLocationRelativeTo(parent);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(12, 12, 12, 12));
        p.setBackground(new Color(173, 233, 227));

        p.add(new JLabel("Category:"));
        JTextField cat = new JTextField(); cat.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30)); p.add(cat);
        p.add(Box.createRigidArea(new Dimension(0, 8)));
        p.add(new JLabel("Amount (Rs):"));
        JTextField amt = new JTextField(); amt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30)); p.add(amt);
        p.add(Box.createRigidArea(new Dimension(0, 8)));
        p.add(new JLabel("Note (optional):"));
        JTextField note = new JTextField(); note.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30)); p.add(note);
        p.add(Box.createRigidArea(new Dimension(0, 12)));

        JButton addBtn = new JButton("Add"); styleBtn(addBtn); p.add(addBtn);

        addBtn.addActionListener(ev -> {
            System.out.println("[DEBUG] Add clicked");
            try {
                String c = cat.getText().trim();
                if (c.isEmpty()) { JOptionPane.showMessageDialog(d, "Enter category"); return; }
                String as = amt.getText().trim().replaceAll(",", "");
                double a;
                try { a = Double.parseDouble(as); } catch (NumberFormatException nfe) { JOptionPane.showMessageDialog(d, "Invalid amount"); System.err.println("[DEBUG] NumberFormatException: " + nfe.getMessage()); return; }
                String n = note.getText().trim();
                String type = "Want";
                try { type = new NeedsWantsClassifier().classify(c); } catch (Throwable t) { System.err.println("[DEBUG] classifier failed: " + t); type = "Want"; }

                Expense ex = new Expense(LocalDate.now(), c, a, type, n);
                userData.expenses.add(ex);
                userData.budget.addSpent(c, a);

                updateBadgeProgress();
                boolean saved = storage.save(userData.username, userData.monthlyIncome, userData.budget, userData.expenses, userData.badges);
                System.out.println("[DEBUG] saved? " + saved);

                double limit = userData.budget.getLimit(c);
                double spent = userData.budget.getSpent(c);
                if (limit > 0 && spent > limit) {
                    double over = spent - limit;
                    JOptionPane.showMessageDialog(d,
                            "⚠️ WARNING: You have exceeded your budget for \"" + c + "\" by Rs " + String.format("%.2f", over),
                            "Budget Exceeded", JOptionPane.WARNING_MESSAGE);
                    applyBadgePenalty("Budget Breeze");
                }

                JOptionPane.showMessageDialog(d, "Added: " + ex.toString());
                d.dispose();

                // refresh dashboard
                for (Frame f : Frame.getFrames()) {
                    if (f instanceof Frame) {
                        Frame fr = (Frame) f;
                        if (fr.getTitle() != null && fr.getTitle().startsWith("PocketPal - Dashboard (" + currentUser)) fr.dispose();
                    }
                }
                SwingUtilities.invokeLater(this::showDashboard);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(d, "Error: " + ex.getMessage());
            }
        });

        d.add(p);
        d.setVisible(true);
    }

    // ------------------- View Expenses -------------------
    void showExpensesDialog(JFrame parent) {
        JDialog d = new JDialog(parent, "Expenses", true);
        d.setSize(820, 460);
        d.setLocationRelativeTo(parent);

        String[] cols = {"Date", "Category", "Amount", "Type", "Note"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (Expense e : userData.expenses) model.addRow(new Object[]{ e.getDate().toString(), e.getCategory(), String.format("%.2f", e.getAmount()), e.getType(), e.getNote() });
        JTable table = new JTable(model);
        d.add(new JScrollPane(table));
        d.setVisible(true);
    }

    // ------------------- Budgets -------------------
    void showBudgetsDialog(JFrame parent) {
        JDialog d = new JDialog(parent, "Budgets", true);
        d.setSize(640, 420);
        d.setLocationRelativeTo(parent);

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        root.setBackground(new Color(173, 233, 227));

        String[] cols = {"Category", "Limit (Rs)", "Spent (Rs)", "Progress"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (String cat : userData.budget.getLimitsCopy().keySet()) {
            double lim = userData.budget.getLimit(cat); double sp = userData.budget.getSpent(cat);
            double pct = lim == 0 ? 0 : (sp/lim)*100.0;
            model.addRow(new Object[]{ cat, String.format("%.2f", lim), String.format("%.2f", sp), String.format("%.0f%%", Math.min(100.0,pct)) });
        }
        JTable table = new JTable(model);
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(173, 233, 227));
        JTextField catF = new JTextField(12); JTextField limF = new JTextField(8);
        JButton set = new JButton("Set/Update"); styleBtn(set);
        set.addActionListener(e -> {
            try { String c = catF.getText().trim(); double l = Double.parseDouble(limF.getText().trim()); userData.budget.setLimit(c,l); storage.save(userData.username,userData.monthlyIncome,userData.budget,userData.expenses,userData.badges); JOptionPane.showMessageDialog(d,"Saved"); d.dispose(); showBudgetsDialog(parent); } catch(Exception ex) { JOptionPane.showMessageDialog(d,"Invalid"); }
        });
        bottom.add(new JLabel("Category:")); bottom.add(catF); bottom.add(new JLabel("Limit:")); bottom.add(limF); bottom.add(set);
        root.add(bottom, BorderLayout.SOUTH);

        d.add(root);
        d.setVisible(true);
    }

    // ------------------- Badges -------------------
    void showBadgesDialog(JFrame parent) {
        updateBadgeProgress();
        JDialog d = new JDialog(parent, "Badges", true);
        d.setSize(560, 420);
        d.setLocationRelativeTo(parent);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(12, 12, 12, 12));
        p.setBackground(new Color(173, 233, 227));

        StringBuilder earned = new StringBuilder();
        int count = 0;
        for (Badge b : userData.badges.values()) if (b.isUnlocked()) { earned.append(b.getName()).append(", "); count++; }
        p.add(new JLabel("Badges earned: " + (count==0? "None": earned.toString())));
        p.add(Box.createRigidArea(new Dimension(0,8)));

        for (String name : badgeManager.badgeNames()) {
            Badge b = badgeManager.getBadge(name);
            JPanel row = new JPanel(new BorderLayout(8,8));
            row.setBackground(new Color(173,233,227));
            JLabel nameL = new JLabel(b.getName());
            JProgressBar pb = new JProgressBar(0,100); pb.setValue((int)b.getProgress()); pb.setStringPainted(true);
            JButton s = new JButton("Suggest"); styleBtn(s);
            s.addActionListener(ev -> {
                ExpenseManager em = new ExpenseManager(userData.budget);
                for (Expense ex : userData.expenses) em.addExpense(ex.getCategory(), ex.getAmount(), ex.getType(), ex.getNote());
                String suggestion = badgeManager.getSuggestionFor(b.getName(), em, userData.budget);
                JOptionPane.showMessageDialog(d, suggestion);
            });
            row.add(nameL, BorderLayout.WEST); row.add(pb, BorderLayout.CENTER); row.add(s, BorderLayout.EAST);
            p.add(row); p.add(Box.createRigidArea(new Dimension(0,8)));
        }

        d.add(new JScrollPane(p));
        d.setVisible(true);
    }

    // ------------------- Reports (weekly/monthly; export) -------------------
    void showReportDialog(JFrame parent) {
        JDialog d = new JDialog(parent, "Reports (Weekly/Monthly)", true);
        d.setSize(700, 520);
        d.setLocationRelativeTo(parent);

        java.util.List<Expense> expenses = userData.expenses;
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(7);
        LocalDate monthAgo = today.minusDays(30);

        double weekTotal = 0, monthTotal = 0;
        Map<String, Double> weekCat = new LinkedHashMap<>();
        Map<String, Double> monthCat = new LinkedHashMap<>();
        double weekNeeds = 0, weekWants = 0, monthNeeds = 0, monthWants = 0;

        for (Expense e : expenses) {
            LocalDate ddate = e.getDate();
            if (!ddate.isBefore(weekAgo)) {
                weekTotal += e.getAmount();
                weekCat.put(e.getCategory(), weekCat.getOrDefault(e.getCategory(), 0.0) + e.getAmount());
                if ("Need".equalsIgnoreCase(e.getType())) weekNeeds += e.getAmount(); else weekWants += e.getAmount();
            }
            if (!ddate.isBefore(monthAgo)) {
                monthTotal += e.getAmount();
                monthCat.put(e.getCategory(), monthCat.getOrDefault(e.getCategory(), 0.0) + e.getAmount());
                if ("Need".equalsIgnoreCase(e.getType())) monthNeeds += e.getAmount(); else monthWants += e.getAmount();
            }
        }

        // make final copies for lambda capture
        final double fWeekTotal = weekTotal;
        final double fMonthTotal = monthTotal;
        final double fWeekNeeds = weekNeeds;
        final double fWeekWants = weekWants;
        final double fMonthNeeds = monthNeeds;
        final double fMonthWants = monthWants;

        JPanel root = new JPanel(new BorderLayout(8,8));
        root.setBorder(new EmptyBorder(12,12,12,12));
        root.setBackground(new Color(173,233,227));

        JTextArea ta = new JTextArea(); ta.setEditable(false);
        StringBuilder sb = new StringBuilder();
        sb.append("Weekly (last 7 days) total: Rs ").append(String.format("%.2f", fWeekTotal)).append("\n");
        sb.append(" - Needs: Rs ").append(String.format("%.2f", fWeekNeeds)).append(" | Wants: Rs ").append(String.format("%.2f", fWeekWants)).append("\n");
        sb.append("Category breakdown (Weekly):\n");
        for (Map.Entry<String,Double> en : weekCat.entrySet()) sb.append("  ").append(en.getKey()).append(": Rs ").append(String.format("%.2f", en.getValue())).append("\n");

        sb.append("\nMonthly (last 30 days) total: Rs ").append(String.format("%.2f", fMonthTotal)).append("\n");
        sb.append(" - Needs: Rs ").append(String.format("%.2f", fMonthNeeds)).append(" | Wants: Rs ").append(String.format("%.2f", fMonthWants)).append("\n");
        sb.append("Category breakdown (Monthly):\n");
        for (Map.Entry<String,Double> en : monthCat.entrySet()) sb.append("  ").append(en.getKey()).append(": Rs ").append(String.format("%.2f", en.getValue())).append("\n");

        ta.setText(sb.toString()); root.add(new JScrollPane(ta), BorderLayout.CENTER);

        JPanel bottom = new JPanel(); bottom.setBackground(new Color(173,233,227));
        JButton exportCSV = new JButton("Export summary to CSV"); styleBtn(exportCSV);
        exportCSV.addActionListener(ae -> {
            String fname = "data/report_" + currentUser + "_" + LocalDate.now().toString() + ".csv";
            try (PrintWriter pw = new PrintWriter(new FileWriter(fname))) {
                pw.println("Period,Total,Needs,Wants");
                pw.println("Weekly," + fWeekTotal + "," + fWeekNeeds + "," + fWeekWants);
                pw.println("Monthly," + fMonthTotal + "," + fMonthNeeds + "," + fMonthWants);
                JOptionPane.showMessageDialog(d, "Report exported: " + fname);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Export failed: " + ex.getMessage());
            }
        });
        bottom.add(exportCSV);
        root.add(bottom, BorderLayout.SOUTH);

        d.add(root);
        d.setVisible(true);
    }

    // ------------------- EMI Calculator Dialog -------------------
    // parent can be null — we accept a parent frame for positioning
    void showEmiDialog(Frame parent) {
        JDialog d = new JDialog(parent, "EMI Calculator", true);
        d.setSize(420, 340);
        d.setLocationRelativeTo(parent);

        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(new EmptyBorder(15,15,15,15));
        root.setBackground(new Color(173,233,227));

        root.add(new JLabel("Principal Amount (₹):"));
        JTextField t1 = new JTextField(); t1.setMaximumSize(new Dimension(Integer.MAX_VALUE,28));
        root.add(t1); root.add(Box.createVerticalStrut(8));

        root.add(new JLabel("Annual Interest Rate (%):"));
        JTextField t2 = new JTextField(); t2.setMaximumSize(new Dimension(Integer.MAX_VALUE,28));
        root.add(t2); root.add(Box.createVerticalStrut(8));

        root.add(new JLabel("Duration (Months):"));
        JTextField t3 = new JTextField(); t3.setMaximumSize(new Dimension(Integer.MAX_VALUE,28));
        root.add(t3); root.add(Box.createVerticalStrut(12));

        JButton calc = new JButton("Calculate EMI"); styleBtn(calc);
        root.add(calc); root.add(Box.createVerticalStrut(12));

        JTextArea out = new JTextArea(5,30); out.setEditable(false); out.setBackground(Color.WHITE);
        root.add(new JScrollPane(out));

        calc.addActionListener(e -> {
            try {
                double P = Double.parseDouble(t1.getText().trim());
                double R = Double.parseDouble(t2.getText().trim());
                int N = Integer.parseInt(t3.getText().trim());
                double emi;
                // use EMICalculator if present, else inline
                try {
                    emi = EMICalculator.calculateEMI(P, R, N);
                } catch (NoSuchMethodError | Exception ex) {
                    double monthlyRate = R / (12.0 * 100.0);
                    if (monthlyRate == 0) emi = P / N;
                    else {
                        double pow = Math.pow(1 + monthlyRate, N);
                        emi = (P * monthlyRate * pow) / (pow - 1);
                    }
                }
                out.setText("Monthly EMI: ₹" + String.format("%.2f", emi));
            } catch (Exception ex) {
                out.setText("Invalid input! Use numeric values.");
            }
        });

        d.add(root);
        d.setVisible(true);
    }

    // small overload to call from dashboard (uses active frame)
    void showEmiDialog(JFrame parent) { showEmiDialog((Frame) parent); }

    // ------------------- Badge update & persistence -------------------
    void updateBadgeProgress() {
        if (badgeManager == null) badgeManager = new BadgeManager(bth);
        ExpenseManager temp = new ExpenseManager(new Budget());
        for (Expense ex : userData.expenses) temp.addExpense(ex.getCategory(), ex.getAmount(), ex.getType(), ex.getNote());
        badgeManager.updateProgressFromExpenseManager(temp, userData.budget);

        for (String name : badgeManager.badgeNames()) {
            Badge b = badgeManager.getBadge(name);
            if (b == null) continue;
            Badge existing = userData.badges.get(name);
            if (existing == null) {
                Badge copy = new Badge(b.getName(), b.getDesc());
                copy.setProgress(b.getProgress());
                userData.badges.put(name, copy);
            } else {
                existing.setProgress(b.getProgress());
            }
        }

        storage.save(userData.username, userData.monthlyIncome, userData.budget, userData.expenses, userData.badges);
    }

    // ------------------- Badge penalty -------------------
    void applyBadgePenalty(String badgeName) {
        try {
            Badge b = userData.badges.get(badgeName);
            if (b == null) {
                Badge source = badgeManager.getBadge(badgeName);
                if (source != null) { b = new Badge(source.getName(), source.getDesc()); userData.badges.put(badgeName, b); }
            }
            if (b != null) {
                double old = b.getProgress();
                double reduced = Math.max(0.0, old - 10.0);
                b.setProgress(reduced);
                System.out.println("[DEBUG] Applied badge penalty to " + badgeName + ": " + old + " -> " + b.getProgress());
                storage.save(userData.username, userData.monthlyIncome, userData.budget, userData.expenses, userData.badges);
            } else {
                System.out.println("[DEBUG] Badge for penalty not found: " + badgeName);
            }
        } catch (Exception e) {
            System.err.println("[DEBUG] applyBadgePenalty failed: " + e);
            e.printStackTrace();
        }
    }
}
