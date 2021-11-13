package com.mathmaster13.fynotekword;
import java.util.HashMap;
import java.math.BigInteger;

public class FynotekWord {
  private String beginning;
  private String vowels;
  private String end;
  private boolean proper;
  private char markVowel; // This class expects you to only create objects from root words, not ablauted forms. Create ablauted words with ablaut(), and the method will mark the word as such.

  // Constants
  private static final String[] digitList = {"", "ay", "fo", "us", "nos", "pur"};

  private static final String[] binaryList = {"po", "pura", "poña", "sola", "manta", "tauwa"};
  
  private static final BigInteger MAX_MAGNITUDE = new BigInteger(new byte[]{43, 86, -44, -81, -113, 121, 50, 39, -116, 121, 126, -67, 0, -1, -1, -1, -1, -1, -1, -1, -1}); // The biggest number supported by the number system.
  
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
  
  private static HashMap<Character, Character> hypoTenseList = new HashMap<Character, Character>(); // hypothetical tenses
  static {
    hypoTenseList.put('p', 'a');
    hypoTenseList.put('a', 'e');
    hypoTenseList.put('f', 'u');
    hypoTenseList.put('g', 'r');
  }

  private static HashMap<Character, Character> tenseList = new HashMap<Character, Character>();
  static {
    tenseList.put('p', '\u0000');
    tenseList.put('a', 'i');
    tenseList.put('f', 'o');
    tenseList.put('g', 'y');
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
  public FynotekWord(FynotekWord word) {
    beginning = word.beginning;
    vowels = word.vowels;
    end = word.end;
    markVowel = word.markVowel;
    proper = word.proper;
  }

  // Private constructors
  private FynotekWord(String a, String b, String c, char mark, boolean isProper) {
    beginning = a;
    vowels = b;
    end = c;
    markVowel = mark;
    proper = isProper;
  }
  private FynotekWord(String word, char mark, boolean isProper) {
    markVowel = mark;
    proper = isProper;
    if (word == null || word.isEmpty()) {
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

  // Internal-use methods
  private FynotekWord ablaut(char vowel) {
    if (vowel == '\u0000') return this;
    if (vowels.isEmpty()) return new FynotekWord(beginning, vowels, end, markVowel, proper);
    if (this.toString().equals("folo") && !proper) {
      // "folo" is a special case and cannot be conjugated for nominative, so the accusative is the root form.
      if (vowel == 'a') return new FynotekWord("fol", "a", "", 'a', false);
      if (vowel == 'i') return new FynotekWord("fol", "i", "", 'i', false);
      else return new FynotekWord("fol", "o", "", 'o', false);
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
    return new FynotekWord(beginning, newVowels, end, vowel, proper);
  }
  private FynotekWord properSuffix(char vowel) {
    String suffix = Character.toString(vowel);
    if (end.length() == 0 && vowels.length() >= 2)
      suffix = "n" + suffix;
    return new FynotekWord(this.toString() + suffix, vowel, proper);
  }
  private static boolean isVowel(char letter) {
    for (char i : vowelList) {
      if (letter == i) return true;
    }
    return false;
  }
  private static boolean isStop(char letter) {
    for (char i : stopList) {
      if (letter == i) return true;
    }
    return false;
  }

  // Public methods
  public void setProper(boolean isProper) { // Old function, not really needed much nowadays, but still there.
    proper = isProper;
  }
  public boolean getProper() {
    return proper;
  }
  public FynotekWord nounCase(char caseOfNoun) {
    if (!(caseOfNoun == 'a' || caseOfNoun == 'd' || caseOfNoun == 'g')) return this;
    char caseLetter = caseList.get(caseOfNoun);
    // While there is an actual suffix function, I prefer to leave this simplified ome in for speed.
    return (proper ? this.properSuffix(caseLetter) : this.ablaut(caseLetter));
  }
  public FynotekWord verbTense(char tenseOfVerb, boolean hypothetical) { // 'a' is used for the past tense.
    if (!(tenseOfVerb == 'p' || tenseOfVerb == 'a' || tenseOfVerb == 'f' || tenseOfVerb == 'g')) return this;
    return (hypothetical ? this.ablaut(hypoTenseList.get(tenseOfVerb)) : this.ablaut(tenseList.get(tenseOfVerb)));
  }
  public FynotekWord verbTense(char tenseOfVerb) {
    return this.verbTense(tenseOfVerb, false);
  }
  public FynotekWord suffix(String suffix) {
    String output = this.toString();
    if (end.isEmpty()) {
      // Check for VVV sequence
      if (isVowel(suffix.charAt(0)) && ((vowels.length() >= 2) || (suffix.length() >= 2 && (isVowel(suffix.charAt(1)))))) {
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
    return new FynotekWord(output, markVowel, proper);
  }
  // The prefix function just calls the suffix function on the reverse of the input, then reverses it back.
  public FynotekWord prefix(String prefix) {
    StringBuilder temp = new StringBuilder(this.toString());
    FynotekWord reverseWord = new FynotekWord(temp.reverse().toString());
    temp = new StringBuilder(prefix);
    temp = new StringBuilder(reverseWord.suffix(temp.reverse().toString()).toString());
    return new FynotekWord(temp.reverse().toString(), markVowel, proper);
  }
  public FynotekWord match(FynotekWord word) {
    return (word.proper ? this.properSuffix(word.markVowel) : this.ablaut(word.markVowel));
  }
  public boolean marked() {
    return (markVowel != '\u0000');
  }

  // Static methods
  public static String number(BigInteger num) {
    if (num == null || num.abs().compareTo(MAX_MAGNITUDE) > 0) return "";
    return number(num.toString(6), (num.signum() == -1));
  }
  public static String number(long num) {
    return number(Long.toString(Math.abs(num), 6), (Math.signum(num) == -1));
  }
  private static String number(String seximalString, boolean isNegative) {
    if (seximalString.equals("0")) return "fui";
    String output = "";
    if (isNegative) output = "ñy ";
    for (int i = 0; i < seximalString.length(); i++) {
      int seximalDigit = seximalString.charAt(i) - 48;
      if (seximalDigit == 0) continue;
      output += (digitList[seximalDigit] + binarySuffix(seximalString.length() - i - 1) + " ");
    }
    return output.trim();
  }
  private static String binarySuffix(int num) {
    String output = "";
    for (int i = 0; i <= 5; i++) {
      if (((num >> i) & 1) == 1) output += binaryList[i];
    }
    return output;
  }
}
