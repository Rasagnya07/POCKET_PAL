//UtilsFileHelpers
// CO Mapping: CO1, CO6
import java.io.*;
import java.util.*;

public class UtilsFileHelpers {
    public static List<String> readAllLines(String filename) {
        List<String> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) list.add(line);
        } catch (Exception e) { }
        return list;
    }

    public static void writeAllLines(String filename, List<String> lines) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            for (String l : lines) pw.println(l);
        } catch (Exception e) { System.out.println(ConsoleColors.red("Failed to save: "+filename)); }
    }

    public static void appendLine(String filename, String line) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename, true))) {
            pw.println(line);
        } catch (Exception e) { System.out.println(ConsoleColors.red("Failed to append: "+filename)); }
    }
}
