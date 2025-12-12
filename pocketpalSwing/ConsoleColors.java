//ConsoleColors
// CO Mapping: CO1
public class ConsoleColors {
    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    public static String green(String s){ return BRIGHT_GREEN()+s+RESET; }
    public static String red(String s){ return BRIGHT_RED()+s+RESET; }
    public static String yellow(String s){ return BRIGHT_YELLOW()+s+RESET; }
    public static String blue(String s){ return BRIGHT_BLUE()+s+RESET; }
    public static String cyan(String s){ return BRIGHT_CYAN()+s+RESET; }
    public static String purple(String s){ return BRIGHT_PURPLE()+s+RESET; }
    public static String bold(String s){ return "\u001B[1m"+s+RESET; }

    private static String BRIGHT_GREEN(){ return "\u001B[92m"; }
    private static String BRIGHT_RED(){ return "\u001B[91m"; }
    private static String BRIGHT_YELLOW(){ return "\u001B[93m"; }
    private static String BRIGHT_BLUE(){ return "\u001B[94m"; }
    private static String BRIGHT_CYAN(){ return "\u001B[96m"; }
    private static String BRIGHT_PURPLE(){ return "\u001B[95m"; }
}
