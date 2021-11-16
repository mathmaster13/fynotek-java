import java.util.Scanner;
import com.mathmaster13.fynotek.FynotekWord;

public class ConjugationDemo {
  static Scanner input = new Scanner(System.in);
  public static void main(String[] args) {
    FynotekWord word = new FynotekWord(prompt("Enter a fynotek root:  "), getProper());
    System.out.println();
    boolean isFolo = !word.isProper() && word.toString().equals("folo");
    
    // Noun cases
    System.out.println("Noun Cases:");
    System.out.println("Nominative:  " + (isFolo ? "N/A" : word.nounCase('n')));
    System.out.println("Accusative:  " + word.nounCase('a'));
    System.out.println("Genitive:  " + word.nounCase('g'));
    System.out.println("Dative:  " + word.nounCase('d'));
    System.out.println();

    // Verb tenses or modifier forms, if the word is not a proper noun or "folo"
    String infoString = (word.isProper() || isFolo ? "Verb Modifier Forms:" : "Verb Tenses:");
    System.out.println("Non-Hypothetical " + infoString);
    System.out.println("Present:  " + word.verbTense('p', false));
    System.out.println("Past:  " + word.verbTense('a', false));
    System.out.println("Future:  " + word.verbTense('f', false));
    System.out.println("Gnomic:  " + word.verbTense('g', false));
    System.out.println();
      
    System.out.println("Hypothetical " + infoString);
    System.out.println("Present:  " + word.verbTense('p', true));
    System.out.println("Past:  " + word.verbTense('a', true));
    System.out.println("Future:  " + word.verbTense('f', true));
    System.out.println("Gnomic:  " + word.verbTense('g', true));
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