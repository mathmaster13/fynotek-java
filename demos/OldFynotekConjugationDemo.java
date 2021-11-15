import java.util.Scanner;

public class OldFynotekConjugationDemo {
  static Scanner input = new Scanner(System.in);
  public static void main(String[] args) {
    OldFynotekWord word = new OldFynotekWord(prompt("Enter an old fynotek root:  "));
    System.out.println();
    System.out.println("Non-Hypothetical Tenses:");
    System.out.println("Present:  " + word.verbTense('p', false));
    System.out.println("Past:  " + word.verbTense('a', false));
    System.out.println("Future:  " + word.verbTense('f', false));
    System.out.println("Gnomic:  " + word.verbTense('g', false));
    System.out.println();
      
    System.out.println("Hypothetical Tenses:");
    System.out.println("Present:  " + word.verbTense('p', true));
    System.out.println("Past:  " + word.verbTense('a', true));
    System.out.println("Future:  " + word.verbTense('f', true));
    System.out.println();
  }

  // Convenience functions
  public static String prompt(String message) {
    System.out.print(message);
    return input.next();
  }
}