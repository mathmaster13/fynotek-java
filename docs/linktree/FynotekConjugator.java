import java.util.Scanner;
import java.util.HashMap;

// The Fynotek Conjugator for linktree people!
public class FynotekConjugator {
  static Scanner input = new Scanner(System.in);
  public static void main(String[] args) {
    ModernFynotekWord word = new ModernFynotekWord(prompt("Enter a fynotek root:  "), getProper());
    System.out.println();
    boolean showVerbs = (!word.isProper() && !word.toString().equals("folo")); // true if the "Verb Tenses" section should be shown.
    
    // Noun cases
    if (showVerbs) System.out.println("Noun Cases:");
    System.out.println("Nominative:  " + ((word.toString().equals("folo") && !word.isProper()) ? "N/A" : word.nounCase('n')));
    System.out.println("Accusative:  " + word.nounCase('a'));
    System.out.println("Genitive:  " + word.nounCase('g'));
    System.out.println("Dative:  " + word.nounCase('d'));
    System.out.println();

    // Verb tenses, if the word is not a proper noun or "folo"
    if (showVerbs) {
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
      System.out.println("Gnomic:  " + word.verbTense('g', true));
      System.out.println();
    }
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


class ModernFynotekWord {
  private String beginning;
  private String vowels;
  private String end;
  private boolean proper;

  // Constants
  private static final char[] vowelList = { 'a', 'e', 'i', 'o', 'u', 'y' };
  
  private static HashMap<Character, String> ablautList = new HashMap<Character, String>();
  static {
    ablautList.put('a', "e");
    ablautList.put('e', "a");
    ablautList.put('i', "y");
    ablautList.put('y', "i");
    ablautList.put('o', "u");
    ablautList.put('u', "o");
  }
  
  private static HashMap<Character, Character> caseList = new HashMap<Character, Character>();
  static {
    caseList.put('a', 'o');
    caseList.put('g', 'i');
    caseList.put('d', 'a');
  }

  private static HashMap<Character, Character> tenseList = new HashMap<Character, Character>();
  static {
    tenseList.put('p', '\u0000');
    tenseList.put('a', 'i');
    tenseList.put('f', 'o');
    tenseList.put('g', 'y');
  }

  private static HashMap<Character, Character> hypoTenseList = new HashMap<Character, Character>(); // hypothetical                                                                      tenses
  static {
    hypoTenseList.put('p', 'a');
    hypoTenseList.put('a', 'e');
    hypoTenseList.put('f', 'u');
    hypoTenseList.put('g', 'r');
  }

  @Override
  public String toString() {
    return beginning + vowels + end;
  }
  
  // Public constructors
  public ModernFynotekWord(String word, boolean isProper) {
    word = word.trim();
    proper = isProper;
    if (word.isEmpty()) { // If you want to re-add the null check, change the condition to (word == null || word.isEmpty())
      beginning = vowels = end = "";
    } else if (word.length() == 1) {
      if (isVowel(word.charAt(0))) {
        vowels = word;
        beginning = end = "";
      } else {
        end = word;
        beginning = vowels = "";
      }
    } else {
      int vowelIndex = 0;
      int vowelLength = 0;
      for (int i = word.length() - 1; i >= 0; i--) {
        if (isVowel(word.charAt(i))) {
          if (isVowel(word.charAt(i - 1))) {
            vowelIndex = i - 1;
            vowelLength = 2;
          } else {
            vowelIndex = i;
            vowelLength = 1;
          }
          break;
        }
      }
      beginning = word.substring(0, vowelIndex);
      vowels = word.substring(vowelIndex, vowelIndex + vowelLength);
      end = word.substring(vowelIndex + vowelLength, word.length());
    }
  }

  // Private constructors
  private ModernFynotekWord(String a, String b, String c, boolean isProper) {
    beginning = a;
    vowels = b;
    end = c;
    proper = isProper;
  }
  
  // Internal-use methods
  private static boolean isVowel(char letter) {
    for (char i : vowelList) {
      if (letter == i)
        return true;
    }
    return false;
  }
  private ModernFynotekWord ablaut(char vowel) {
    if (vowel == '\u0000') return this;
    if (vowels.isEmpty()) return new ModernFynotekWord(beginning, vowels, end, proper);
    if (this.toString().equals("folo") && !proper) {
      // "folo" is a special case and cannot be conjugated for nominative, so the accusative is the root form.
      if (vowel == 'a') return new ModernFynotekWord("fol", "a", "", false);
      if (vowel == 'i') return new ModernFynotekWord("fol", "i", "", false);
      else return new ModernFynotekWord("fol", "o", "", false);
    }
    String newVowels = vowels;
    if (vowel != 'r') { // 'r' is for reduplcation
      if (vowels.charAt(vowels.length() - 1) != vowel) {
        newVowels = (vowels.length() == 1 ? Character.toString(vowel) : (Character.toString(vowels.charAt(0)) + Character.toString(vowel)));
      } else {
        newVowels += ablautList.get(vowel);
        if (newVowels.length() > 2) {
          newVowels = newVowels.substring(newVowels.length()-2, newVowels.length());
        }
      }
    } else {
      if (newVowels.length() == 1 || newVowels.charAt(0) != newVowels.charAt(1) ) {
        newVowels += newVowels.substring(newVowels.length() - 1);
        if (newVowels.length() > 2) {
          newVowels = newVowels.substring(newVowels.length()-2, newVowels.length());
        }
      } else {
        newVowels = newVowels.substring(0, 1);
        }
    }
    return new ModernFynotekWord(beginning, newVowels, end, proper);
  }

  private ModernFynotekWord properSuffix(char vowel) {
    String suffix = Character.toString(vowel);
    if (end.isEmpty() && vowels.length() > 2) suffix = "n" + suffix;
    return new ModernFynotekWord(this.toString() + suffix, true);
  }

  
  // Public methods
  public ModernFynotekWord nounCase(char caseOfNoun) {
    if (!(caseOfNoun == 'a' || caseOfNoun == 'd' || caseOfNoun == 'g')) return this;
    char caseLetter = caseList.get(caseOfNoun);
    // While there is an actual suffix function, I prefer to leave this simplified ome in for speed.
    return (proper ? this.properSuffix(caseLetter) : this.ablaut(caseLetter));
  }

  public ModernFynotekWord verbTense(char tenseOfVerb, boolean hypothetical) { // 'a' is used for the past tense.
    if (!(tenseOfVerb == 'p' || tenseOfVerb == 'a' || tenseOfVerb == 'f' || tenseOfVerb == 'g'))
      return this;
    return (hypothetical ? ablaut(hypoTenseList.get(tenseOfVerb)) : ablaut(tenseList.get(tenseOfVerb)));
  }
  
  public boolean isProper() {
    return proper;
  }
}
