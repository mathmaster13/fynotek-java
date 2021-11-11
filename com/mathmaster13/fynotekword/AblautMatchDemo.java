package com.mathmaster13.fynotekword;
import java.util.Scanner;

public class AblautMatchDemo {
  static Scanner input = new Scanner(System.in);
  public static void main(String[] args) {
    System.out.println("This is a demo for the new FynotekWord class. You will be prompted for a noun and a case to conjugate it to, two suffixes for the noun, and a detached root as a modifier for the noun.");
    FynotekWord word = new FynotekWord(prompt("Enter a fynotek noun:  "), getProper());
    char nounCase = getCase(word);
    String suffix1 = prompt("Enter the first suffix:  ");
    String suffix2 = prompt("Enter the second suffix:  ");
    FynotekWord modifier = new FynotekWord(getModifier(nounCase, word.getProper()));
    FynotekWord inflectedNoun = word.nounCase(nounCase).suffix(suffix1).suffix(suffix2);
    System.out.println(inflectedNoun + " " + modifier.match(inflectedNoun));
  }

  // Convenience functions
  public static String prompt(String message) {
    System.out.print(message);
    return input.next();
  }
  public static char getCase(FynotekWord word) {
    char nounCase;
    boolean isFolo = !word.getProper() && word.toString().equals("folo");
    do {
      System.out.print("What case is this word? (" + (isFolo ? "" : "N = nominative, ") + "A = accusative, D = dative, G = genitive):  ");
      nounCase = Character.toLowerCase(input.next().charAt(0));
    } while (isFolo ? (!(nounCase == 'a' || nounCase == 'd' || nounCase == 'g')) : (!(nounCase == 'n' || nounCase == 'a' || nounCase == 'd' || nounCase == 'g')));
    return nounCase;
  }
  public static String getModifier(char nounCase, boolean proper) {
    String word = prompt("Enter the detached modifier:  ");
    if (nounCase != 'n' || proper) return word;
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
