import java.util.Scanner;

import io.github.mathmaster13.aspenlangs.fynotek.BaseFynotekWord;
import io.github.mathmaster13.aspenlangs.fynotek.OldFynotekWord;

// Formerly OldFynotekConjugationDemo
public class OldFynotekInflectionDemo {
    static Scanner input = new Scanner(System.in);
    public static void main(String[] args) {
        OldFynotekWord word = new OldFynotekWord(prompt("Enter an old fynotek root:  "));
        System.out.println();
        System.out.println("Non-Hypothetical Tenses:");
        System.out.println("Present:  " + word.verbTense(BaseFynotekWord.Tense.PRESENT));
        System.out.println("Past:  " + word.verbTense(BaseFynotekWord.Tense.PAST));
        System.out.println("Future:  " + word.verbTense(BaseFynotekWord.Tense.FUTURE));
        System.out.println();

        System.out.println("Hypothetical Tenses:");
        System.out.println("Present:  " + word.verbTense(BaseFynotekWord.Tense.HYP_PRESENT));
        System.out.println("Past:  " + word.verbTense(BaseFynotekWord.Tense.HYP_PAST));
        System.out.println("Future:  " + word.verbTense(BaseFynotekWord.Tense.HYP_FUTURE));
        System.out.println();

        System.out.println("Gnomic Tense:  " + word.verbTense(BaseFynotekWord.Tense.GNOMIC));
        System.out.println();
    }

    // Convenience functions
    public static String prompt(String message) {
        System.out.print(message);
        return input.next();
    }
}