package com.mathmaster13.fynotekword;
import java.util.HashMap;

// Note: This class was designed such that each function creates a new object. If you don't like this behavior and want the class to modify the existing object, feel free to modify the class.
public class FynotekWord {
  private String beginning;
  private String vowels;
  private String end;
  private boolean proper;
  private char ablautMode; // This class expects you to only create objects from root words, not ablauted forms. Create ablauted words with ablaut(), and the method will mark the word as such.

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

  private static HashMap<Integer, String> numberList = new HashMap<Integer, String>();
  static {
    numberList.put(0, "");
    numberList.put(1, "ay");
    numberList.put(2, "fo");
    numberList.put(3, "us");
    numberList.put(4, "nos");
    numberList.put(5, "pur");
  }

  
  @Override
  public String toString() {
    return (beginning + vowels + end);
  }
  

  // Public constructors
  public FynotekWord(String word, boolean isProper) {
    this(word, '\u0000', isProper);
  }
  public FynotekWord(String word) {
    this(word, '\u0000', false);
  }

  // Private constructors
  private FynotekWord(String a, String b, String c, char ablaut, boolean isProper) {
    beginning = a;
    vowels = b;
    end = c;
    ablautMode = ablaut;
    proper = isProper;
  }
  private FynotekWord(String word, char ablaut, boolean isProper) {
    ablautMode = ablaut;
    proper = isProper;
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

  // Internal-use methods
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

  // Public methods
  public FynotekWord ablaut(char vowel) {
    if (vowel == '\u0000')
      return this;
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
    return new FynotekWord(beginning, newVowels, end, vowel, proper);
  }
  public void setProper(boolean isProper) { // Old function, not really needed much nowadays, but still there.
    proper = isProper;
  }
  public boolean getProper() {
    return proper;
  }
  public FynotekWord nounCase(char caseOfNoun) {
    if (caseOfNoun == 'n') {
      return this;
    } else {
      if (this.toString().equals("folo") && !proper) {
        // "folo" is a special case and cannot be conjugated for nominative, so the accusative is the root form.
        if (caseOfNoun == 'a') return new FynotekWord("fol", "o", "", 'o', false);
        if (caseOfNoun == 'g') return new FynotekWord("fol", "i", "", 'i', false);
        else return new FynotekWord("fol", "a", "", 'a', false);
      }
      String caseLetter = caseList.get(caseOfNoun);
      if (proper) { // While there is an actual suffix function, I prefer to leave this simplified ome in for speed.
        if (end.length() == 0 && vowels.length() >= 2)
          caseLetter = "n" + caseLetter;
        return new FynotekWord(this.toString() + caseLetter, proper);
      } else {
        return this.ablaut(caseLetter.charAt(0));
      }
    }
  }
  public FynotekWord verbTense(char tenseOfVerb, boolean hypothetical) { // 'a' is used for the past tense.
    if (hypothetical) {
      return this.ablaut(hypoTenseList.get(tenseOfVerb).charAt(0));
    } else {
      if (tenseOfVerb == 'p') {
        return this;
      } else {
        return this.ablaut(tenseList.get(tenseOfVerb).charAt(0));
      }
    }
  }
  public FynotekWord suffix(String suffix) {
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
    return new FynotekWord(output, ablautMode, proper);
  }
  // The prefix function just calls the suffix function on the reverse of the input, then reverses it back.
  public FynotekWord prefix(String prefix) {
    StringBuilder temp = new StringBuilder(this.toString());
    FynotekWord reverseWord = new FynotekWord(temp.reverse().toString());
    temp = new StringBuilder(prefix);
    temp = new StringBuilder(reverseWord.suffix(temp.reverse().toString()).toString());
    return new FynotekWord(temp.reverse().toString(), ablautMode, proper);
  }
  public FynotekWord matchAblaut(FynotekWord word) {
    return this.ablaut(word.ablautMode);
  }
  public boolean hasAblaut() {
    return (ablautMode != '\u0000');
  }
}
