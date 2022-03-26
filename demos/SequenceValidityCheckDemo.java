import java.util.Scanner;
import com.mathmaster13.fynotek.FynotekWord;

public class SequenceValidityCheckDemo {
    public static final Scanner input = new Scanner(System.in);
    public static void main(String[] args) {
        System.out.println((FynotekWord.isValidSequence(prompt("Enter any string:  ")) ? "This is a valid Fynotek sequence!" : "This is an invalid Fynotek sequence."));
    }

    // Convenience functions
    public static String prompt(String message) {
        System.out.print(message);
        return input.nextLine();
    }
}