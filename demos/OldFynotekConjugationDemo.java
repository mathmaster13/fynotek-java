import java.util.Scanner;

import com.mathmaster13.fynotek.FynotekParent;
import com.mathmaster13.fynotek.OldFynotekWord;

public class OldFynotekConjugationDemo {
    static Scanner input = new Scanner(System.in);
    public static void main(String[] args) {
        OldFynotekWord word = new OldFynotekWord(prompt("Enter an old fynotek root:  "));
        System.out.println();
        System.out.println("Non-Hypothetical Tenses:");
        System.out.println("Present:  " + word.verbTense(FynotekParent.Tense.PRESENT));
        System.out.println("Past:  " + word.verbTense(FynotekParent.Tense.PAST));
        System.out.println("Future:  " + word.verbTense(FynotekParent.Tense.FUTURE));
        System.out.println("Gnomic:  " + word.verbTense(FynotekParent.Tense.GNOMIC));
        System.out.println();

        System.out.println("Hypothetical Tenses:");
        System.out.println("Present:  " + word.verbTense(FynotekParent.Tense.HYP_PRESENT));
        System.out.println("Past:  " + word.verbTense(FynotekParent.Tense.HYP_PAST));
        System.out.println("Future:  " + word.verbTense(FynotekParent.Tense.HYP_FUTURE));
        System.out.println();
    }

    // Convenience functions
    public static String prompt(String message) {
        System.out.print(message);
        return input.next();
    }
}