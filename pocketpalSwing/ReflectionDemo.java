//ReflectionDemo
// CO Mapping: CO5
import java.lang.reflect.*;
import java.time.LocalDate;

public class ReflectionDemo {
    public static void runDemo() {
        try {
            Class<?> cls = Class.forName("Expense");
            System.out.println("ReflectionDemo: class name = " + cls.getName());

            Field[] fields = cls.getDeclaredFields();
            System.out.println("Fields:");
            for (Field f : fields) System.out.println(" - " + f.getName() + " : " + f.getType().getSimpleName());

            Constructor<?> ctor = cls.getConstructor(java.time.LocalDate.class, String.class, double.class, String.class, String.class);
            Object instance = ctor.newInstance(LocalDate.now(), "demo", 42.0, "Need", "ref-demo");
            Method toStr = cls.getMethod("toString");
            Object res = toStr.invoke(instance);
            System.out.println("toString() via reflection -> " + res.toString());
        } catch (Exception e) {
            System.out.println("Reflection demo failed: " + e);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        runDemo();
    }
}
