// Third iteration, adding limited suffix and prefix functionality (you can't use the ablaut and affix functions together), and adding a check for \u0000 in the ablaut function (might use that later)
import java.util.HashMap;
import java.util.Scanner; // Only necessary for user input.
public class FynotekV3 {
  static Scanner input = new Scanner(System.in);
  public static void main(String[] args) {
    FynotekWord word = new FynotekWord(prompt("Enter a fynotek root:  "));
    String affix = prompt("Enter an affix:  ");
    System.out.println(isPrefix() ? word.prefix(affix) : word.suffix(affix));
  }

// That's all the user input code. The rest of these are functions to make said user input code easier to read.
  public static String prompt(String message) {
    System.out.print(message);
    return input.next();
  }
  
  public static boolean isPrefix() {
    char affixType;
    do {
      System.out.print("Is this a prefix or suffix? (P = prefix, S = suffix):  ");
      affixType = Character.toLowerCase(input.next().charAt(0));
    } while (!(affixType == 'p' || affixType == 's' ));
    return (affixType == 'p');
  }
}


/* The only notable things added here are the prefix() and suffix() functions. Next iteration might have a bit more in it. */
class FynotekWord {
  private String beginning;
  private String vowels;
  private String end;
  private boolean proper = false;
  private char ablautMode;
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
    ablautMode = '\u0000';
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
  private static boolean isStop(char letter) {
    boolean output = false;
    for (char i : stopList) {
      if (letter == i) {
        output = true;
        break;
      }
    }
    return output;
  }
  public String ablaut(char vowel) {
    if (vowel == '\u0000')
      return this.toString();
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
      if (proper) { // While there is an actual suffix function, I prefer to leave this simplified ome in for speed.
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
  public String suffix(String suffix) {
    String output = this.toString();
    if (end.length() == 0) {
      // Check for VVV sequence
      if (isVowel(suffix.charAt(0)) && ((vowels.length() >= 2) || (isVowel(suffix.charAt(1))))) {
        output += ("n" + suffix);
      } else {
        output += suffix;
      }
    } else {
      // Check for CCCC sequence (after the ||) or stop+stop sequence (before the ||)
      if (((isStop(end.charAt(end.length() - 1)) && isStop(suffix.charAt(0)))) || ((end.length() >= 2) && (!isVowel(suffix.charAt(0))) && (!isVowel(suffix.charAt(1))))) {
        output += ("a" + suffix);
      } else {
        output += suffix;
      }
    }
    return output;
  }
  public String prefix(String prefix) {
    StringBuilder temp = new StringBuilder(this.toString());
    FynotekWord reverseWord = new FynotekWord(temp.reverse().toString());
    temp = new StringBuilder(prefix);
    temp = new StringBuilder(reverseWord.suffix(temp.reverse().toString()));
    return temp.reverse().toString();
  }
}