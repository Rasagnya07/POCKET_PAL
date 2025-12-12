//Expense

// CO Mapping: CO1, CO3, CO4, CO6
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Expense {
    private static final DateTimeFormatter DF = DateTimeFormatter.ISO_LOCAL_DATE;
    private final LocalDate date;
    private final String category;
    private final double amount;
    private final String type; // Need/Want
    private final String note;

    public Expense(LocalDate date, String category, double amount, String type, String note) {
        this.date = date == null ? LocalDate.now() : date;
        this.category = category == null || category.isBlank() ? "Other" : category.trim();
        this.amount = amount;
        this.type = (type==null||type.isBlank())? "Want" : capitalize(type);
        this.note = note==null? "" : note;
    }

    public LocalDate getDate(){ return date; }
    public String getCategory(){ return category; }
    public double getAmount(){ return amount; }
    public String getType(){ return type; }
    public String getNote(){ return note; }

    @Override
    public String toString() {
        return date.format(DF)+" | "+category+" | Rs"+String.format("%.2f",amount)+" | "+type + (note.isEmpty() ? "" : " | "+note);
    }

    public String toCSV() {
        return date.format(DF)+","+escape(category)+","+amount+","+escape(type)+","+escape(note);
    }

    public static Expense fromCSV(String line) {
        try {
            String[] p = line.split(",",5);
            java.time.LocalDate d = java.time.LocalDate.parse(p[0], DF);
            String cat = unescape(p[1]);
            double amt = Double.parseDouble(p[2]);
            String type = unescape(p[3]);
            String note = p.length>=5 ? unescape(p[4]) : "";
            return new Expense(d, cat, amt, type, note);
        } catch (Exception e) { return null; }
    }

    // adapter to implement Persistable if needed
    public String toCSVLine(){ return toCSV(); }

    private static String escape(String s) { return s.replace(",", "%c%"); }
    private static String unescape(String s){ return s.replace("%c%", ","); }
    private static String capitalize(String s){ if(s.isBlank()) return s; return s.substring(0,1).toUpperCase()+s.substring(1).toLowerCase(); }
}
