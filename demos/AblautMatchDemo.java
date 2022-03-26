import java.util.Scanner;
import com.mathmaster13.fynotek.FynotekWord;
import com.mathmaster13.fynotek.FynotekWord.Case;

public class AblautMatchDemo {
    public static final Scanner input = new Scanner(System.in);
    public static void main(String[] args) {
        System.out.println("This is a demo for the new FynotekWord class. You will be prompted for a noun and a case to conjugate it to, two suffixes for the noun, and a detached root as a modifier for the noun.");
        FynotekWord word = new FynotekWord(prompt("Enter a fynotek noun:  "), getProper());
        Case nounCase = getCase(word);
        String suffix1 = prompt("Enter the first suffix:  ");
        String suffix2 = prompt("Enter the second suffix:  ");
        FynotekWord modifier = new FynotekWord(getModifier(nounCase, word.isProper));
        FynotekWord inflectedNoun = word.nounCase(nounCase).suffix(suffix1).suffix(suffix2);
        System.out.println(inflectedNoun + " " + modifier.match(inflectedNoun));
    }

    // Convenience functions
    public static String prompt(String message) {
        System.out.print(message);
        return input.next();
    }
    public static Case getCase(FynotekWord word) {
        char nounCase;
        boolean isFolo = !word.isProper && word.toString().equals("folo");
        do {
            System.out.print("What case is this word? (" + (isFolo ? "" : "N = nominative, ") + "A = accusative, D = dative, G = genitive):  ");
            nounCase = Character.toLowerCase(input.next().charAt(0));
        } while (isFolo ? (!(nounCase == 'a' || nounCase == 'd' || nounCase == 'g')) : (!(nounCase == 'n' || nounCase == 'a' || nounCase == 'd' || nounCase == 'g')));
        return charToCase(nounCase);
    }
    public static Case charToCase(char caseAsChar) {
        switch (caseAsChar) {
            case 'n':
                return Case.NOMINATIVE;
            case 'a':
                return Case.ACCUSATIVE;
            case 'g':
                return Case.GENITIVE;
            case 'd':
                return Case.DATIVE;
        }
        throw new IllegalArgumentException("Invalid character");
    }
    public static String getModifier(Case nounCase, boolean proper) {
        String word = prompt("Enter the detached modifier:  ");
        if (nounCase != Case.NOMINATIVE || proper) return word;
        while (word.equals("folo")) {
            System.out.println("You cannot conjugate \"folo\" in the nominative.  Please enter another word.");
            word = prompt("Enter the detached modifier:  ");
        }
        return word;
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
