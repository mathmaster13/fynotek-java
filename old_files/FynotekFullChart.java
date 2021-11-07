// Second iteration, minor changes
import java.util.HashMap;
import java.util.Scanner; // Only necessary for user input.
public class FynotekFullChart { // All this class does is deliver the prompts to the user. All of the actual processing is done by the FynotekWord class.
  static Scanner input = new Scanner(System.in);
  public static void main(String[] args) {
    FynotekWord word = new FynotekWord(prompt("Enter a fynotek root:  "));
    boolean proper = getProper();
    word.setProper(proper);
    System.out.println();
    System.out.println("Nominative:  " + word.nounCase('n'));
    System.out.println("Accusative:  " + word.nounCase('a'));
    System.out.println("Genitive:  " + word.nounCase('g'));
    System.out.println("Dative:  " + word.nounCase('d'));
    System.out.println();
    if (!proper) {
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

// That's all the user input code. The rest of these are functions to make said user input code easier to read.
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


/* The FynotekWord class contains a bunch of functions to work with words, such as ablaut(), nounCase(), verbTense(), and setProper(). I plan to add more stuff to this in the future, such as a prefix() or suffix() function. */
class FynotekWord {
  private String beginning;
  private String vowels;
  private String end;
  private boolean proper = false;
  private static final char[] vowelList = {'a', 'e', 'i', 'o', 'u', 'y'};
  private static HashMap<Character, String> ablautList = new HashMap<Character, String>();
  static {
    ablautList.put('a', "e");
    ablautList.put('e', "a");
    ablautList.put('i', "y");
    ablautList.put('y', "i");
    ablautList.put('o', "u");
    ablautList.put('u', "o");
  }
    
  private static HashMap<Character, String> caseList = new HashMap<Character, String>();
  static {
    caseList.put('a', "o");
    caseList.put('g', "i");
    caseList.put('d', "a");
  }

  private static HashMap<Character, String> hypoTenseList = new HashMap<Character, String>(); // hypothetical tenses
  static {
    hypoTenseList.put('p', "a");
    hypoTenseList.put('a', "e");
    hypoTenseList.put('f', "u");
    hypoTenseList.put('g', "r");
  }

    private static HashMap<Character, String> tenseList = new HashMap<Character, String>();
  static {
    tenseList.put('a', "i");
    tenseList.put('f', "o");
    tenseList.put('g', "y");
  }
  
  public String toString() {
    return (beginning + vowels + end);
  }
  
  public FynotekWord(String word) {
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
  private static boolean isVowel(char letter) {
    boolean output = false;
    for (char i : vowelList) {
      if (letter == i) {
        output = true;
        break;
      }
    }
    return output;
  }
  public String ablaut(char vowel) {
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
    return beginning + newVowels + end;
  }
  public void setProper(boolean isProper) {
    proper = isProper;
  }
  public String nounCase(char caseOfNoun) {
    String output = "";
    if (caseOfNoun == 'n') {
      output = this.toString();
    } else {
      String caseLetter = caseList.get(caseOfNoun);
      if (proper) { // Making an actual suffix function for all sorts of suffixes will be done at anothet time.
        if (end.length() == 0 && vowels.length() >= 2)
          caseLetter = "n" + caseLetter;
        output = this.toString() + caseLetter;
      } else {
        output = this.ablaut(caseLetter.charAt(0));
      }
    }
    return output;
  }
  public String verbTense(char tenseOfVerb, boolean hypothetical) { // 'a' is used for the past tense.
    String output = "";
    if (hypothetical) {
      output = this.ablaut(hypoTenseList.get(tenseOfVerb).charAt(0));
    } else {
      if (tenseOfVerb == 'p') {
        output = this.toString();
      } else {
        output = this.ablaut(tenseList.get(tenseOfVerb).charAt(0));
      }
    }
    return output;
  }
}