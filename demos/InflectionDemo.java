import java.util.Scanner;
import com.mathmaster13.fynotek.FynotekWord;
import com.mathmaster13.fynotek.FynotekWord.Case;
import com.mathmaster13.fynotek.FynotekParent.Tense;

// Formerly ConjugationDemo
public class InflectionDemo {
    public static final Scanner input = new Scanner(System.in);
    public static void main(String[] args) {
        FynotekWord word = new FynotekWord(prompt("Enter a fynotek root:  "), getProper());
        System.out.println();
        boolean isFolo = word.equals(FynotekWord.FOLO);

        // Noun cases
        System.out.println("Noun Cases:");
        System.out.println("Nominative:  " + (isFolo ? "N/A" : word.nounCase(Case.NOMINATIVE)));
        System.out.println("Accusative:  " + word.nounCase(Case.ACCUSATIVE));
        System.out.println("Genitive:  " + word.nounCase(Case.GENITIVE));
        System.out.println("Dative:  " + word.nounCase(Case.DATIVE));
        System.out.println("Other:  " + word.nounCase(Case.OTHER));
        System.out.println();

        // Verb tenses or modifier forms, if the word is not a proper noun or "folo"
        String infoString = (word.isProper || isFolo ? "Verb Modifier Forms:" : "Verb Tenses:");
        System.out.println("Non-Hypothetical " + infoString);
        System.out.println("Present:  " + word.verbTense(Tense.PRESENT));
        System.out.println("Past:  " + word.verbTense(Tense.PAST));
        System.out.println("Future:  " + word.verbTense(Tense.FUTURE));
        System.out.println("Gnomic:  " + word.verbTense(Tense.GNOMIC));
        System.out.println();

        System.out.println("Hypothetical " + infoString);
        System.out.println("Present:  " + word.verbTense(Tense.HYP_PRESENT));
        System.out.println("Past:  " + word.verbTense(Tense.HYP_PAST));
        System.out.println("Future:  " + word.verbTense(Tense.HYP_FUTURE));
        System.out.println("Gnomic:  " + word.verbTense(Tense.HYP_GNOMIC));
        System.out.println();
    }

    // Convenience functions
    public static String prompt(String message) {
        System.out.print(message);
        return input.next();
    }

    public static boolean getProper() {
        char proper;
        do {
            System.out.print("Is this a proper noun? (Y = yes, N = no):  ");
            proper = Character.toLowerCase(input.next().charAt(0));
        } while (!(proper == 'y' || proper == 'n' ));
        return (proper == 'y');
    }
}