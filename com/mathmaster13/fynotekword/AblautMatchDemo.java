package com.mathmaster13.fynotekword;
import java.util.Scanner;

public class AblautMatchDemo {
  static Scanner input = new Scanner(System.in);
  public static void main(String[] args) {
    System.out.println("This is a demo for the new FynotekWord class. You will be prompted for a (non-proper) noun and a case to conjugate it to, two suffixes for the noun, and a detached modifier for the noun.");
    FynotekWord word = new FynotekWord(prompt("Enter a fynotek noun:  "));
    char nounCase = getCase();
    String suffix1 = prompt("Enter the first suffix:  ");
    String suffix2 = prompt("Enter the second suffix:  ");
    FynotekWord modifier = new FynotekWord(prompt("Enter the detached modifier:  "));
    FynotekWord inflectedNoun = word.nounCase(nounCase).suffix(suffix1).suffix(suffix2);
    System.out.println(inflectedNoun + " " + modifier.matchAblaut(inflectedNoun));
  }

// Convenience functions
  public static String prompt(String message) {
    System.out.print(message);
    return input.next();
  }
  public static char getCase() {
    char nounCase;
    do {
      System.out.print("What case is this word? (N = nominative, A = accusative, D = dative, G = genitive):  ");
      nounCase = Character.toLowerCase(input.next().charAt(0));
    } while (!(nounCase == 'n' || nounCase == 'a' || nounCase == 'd' || nounCase == 'g'));
    return nounCase;
  }
}