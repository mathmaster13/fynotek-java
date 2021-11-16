import java.util.Scanner;
import java.util.HashMap;

/*
A stripped-down version of the FynotekWord class that contains only methods needed to run ConjugationDemo.java (here called FynotekConjugator). Some methods are only partially implemented, because they do not need all of their functionality for this specific program. If you'd like to examine the full FynotekWord library, please use the files on GitHub and not this one.
*/
public class FynotekConjugator {
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


class FynotekWord {
  private String beginning;
  private String vowels;
  private String end;
  private boolean proper;

  // Constants
  private static final char[] vowelList = {'a', 'e', 'i', 'o', 'u', 'y'};
  private static final char[] stopList = {'p', 't', 'k'};
  
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
  public FynotekWord(String word, boolean isProper) {
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
  private FynotekWord(String a, String b, String c, boolean isProper) {
    beginning = a;
    vowels = b;
    end = c;
    proper = isProper;
  }
  
  // Internal-use methods
  private static boolean isVowel(char letter) {
    for (char i : vowelList) if (letter == i) return true;
    return false;
  }
  private static boolean isStop(char letter) {
    for (char i : stopList) if (letter == i) return true;
    return false;
  }
  private FynotekWord ablaut(char vowel) {
    if (vowel == '\u0000') return this;
    if (vowels.isEmpty()) return new FynotekWord(beginning, vowels, end, proper);
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
    return new FynotekWord(beginning, newVowels, end, proper);
  }

  private FynotekWord properSuffix(char vowel) {
    if (vowel == '\u0000') return this;
    if (vowel == 'r') {
      int vowelLength = vowels.length();
      if (vowelLength == 0) return this;
      String temp = (vowelLength == 1 ? vowels : Character.toString(vowels.charAt(vowelLength - 1)));
      return this.suffix(temp + temp);
    } else {
      String suffix = Character.toString(vowel);
      if (end.length() == 0 && vowels.length() >= 2) suffix = "n" + suffix;
      return new FynotekWord(this.toString() + suffix, proper);
  }
}
  
  private FynotekWord suffix(String suffix) {
    if (suffix.isEmpty()) return this;
    String output = this.toString();
    if (end.isEmpty()) {
      // Check for VVV sequence
      if (isVowel(suffix.charAt(0)) && ((vowels.length() >= 2) || (suffix.length() >= 2 && (isVowel(suffix.charAt(1)))))) {
        output += ("n" + suffix);
      } else {
        output += suffix;
      }
    } else {
      // This is so complex that delegating it to the isValidSequence() function may be needed.
      if (!isValidSequence(vowels + end + suffix)) {
        output += ("a" + suffix);
      } else {
        output += suffix;
      }
    }
    return new FynotekWord(output, proper);
  }

  private static boolean isValidSequence(String sequence) {
    sequence = sequence.toLowerCase().trim();
    // Checks for consonant-related phonotactic problems
    int i = 0;
    while (i < sequence.length()) {
      char testChar = sequence.charAt(i);
      // Consonant check
      int j;
      for (j = i; j < sequence.length(); j++)
        if (isVowel(sequence.charAt(j)))
           break;
      if (j - i + ((i == 0 || j == sequence.length()) ? 1 : 0) > 3) return false;
      if (j - i == 0) j++;

      // Stop+Stop check
      boolean stopCheck = isStop(testChar);
      for (int l = i + 1; l < j; l++) {
        boolean currentCharIsStop = isStop(sequence.charAt(l));
        if (stopCheck && currentCharIsStop) return false;
        stopCheck = currentCharIsStop;
      }
      i = j;
    }
    return true;
  }
  
  // Public methods
  public FynotekWord nounCase(char caseOfNoun) {
    if (!(caseOfNoun == 'a' || caseOfNoun == 'd' || caseOfNoun == 'g')) return this;
    char caseLetter = caseList.get(caseOfNoun);
    if (this.toString().equals("folo") && !proper) return (caseOfNoun == 'a' ? this : this.ablaut(caseLetter));
    return (proper ? this.properSuffix(caseLetter) : this.ablaut(caseLetter));
  }

  public FynotekWord verbTense(char tenseOfVerb, boolean hypothetical) { // 'a' is used for the past tense.
    if (!(tenseOfVerb == 'p' || tenseOfVerb == 'a' || tenseOfVerb == 'f' || tenseOfVerb == 'g')) return this;
    if (!proper) return (hypothetical ? ablaut(hypoTenseList.get(tenseOfVerb)) : ablaut(tenseList.get(tenseOfVerb)));
    return (hypothetical ? properSuffix(hypoTenseList.get(tenseOfVerb)) : properSuffix(tenseList.get(tenseOfVerb)));
  }
  
  public boolean isProper() {
    return proper;
  }
}