import java.util.Scanner;
import com.mathmaster13.fynotek.FynotekWord;

public class NumberDemo {
    public static final Scanner input = new Scanner(System.in);
    public static void main(String[] args) {
        int radix = (Character.toLowerCase(prompt("This is a demo for the number function of the FynotekWord class. Would you like to enter a decimal (D) or seximal (S) string? (default is decimal). ").charAt(0)) == 's' ? 6 : 10);
        System.out.println(FynotekWord.number(getNum(radix)));
    }

    // Convenience functions
    public static String prompt(String message) {
        System.out.print(message);
        return input.next();
    }
    public static long getNum(int radix) {
        while (true) {
            long num;
            try {
                num = Long.parseLong(prompt("Enter a number:  "), radix);
                return num;
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }
}