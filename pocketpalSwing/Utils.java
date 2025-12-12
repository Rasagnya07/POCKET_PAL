//Utils
// CO Mapping: CO1, CO3, CO6
import java.util.Scanner;

public class Utils {
    private static final Scanner sc = new Scanner(System.in);

    public static String input(String prompt) {
        System.out.print(ConsoleColors.cyan(prompt));
        return sc.nextLine().trim();
    }

    public static int inputInt(String prompt) {
        while (true) {
            try {
                String s = input(prompt);
                return Integer.parseInt(s);
            } catch (Exception e) {
                System.out.println(ConsoleColors.red("Enter a valid integer."));
            }
        }
    }

    public static double inputDouble(String prompt) {
        while (true) {
            try {
                String s = input(prompt);
                return Double.parseDouble(s);
            } catch (Exception e) {
                System.out.println(ConsoleColors.red("Enter a valid number."));
            }
        }
    }

    public static void pause() {
        System.out.print(ConsoleColors.BLUE + "\nPress Enter to continue..." + ConsoleColors.RESET);
        sc.nextLine();
    }

    public static String progressBar(double percent) {
        int len = 20;
        int filled = (int)Math.round((percent/100.0)*len);
        if (filled<0) filled=0; if (filled>len) filled=len;
        StringBuilder sb = new StringBuilder("[");
        for (int i=0;i<len;i++) sb.append(i<filled ? "#" : "-");
        sb.append("] ").append((int)percent).append("%");
        return sb.toString();
    }
}
