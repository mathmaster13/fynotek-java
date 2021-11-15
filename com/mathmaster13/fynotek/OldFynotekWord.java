package com.mathmaster13.fynotek;
import java.util.HashMap;

/**
A class for handling words in an older version of Fynotek.
@author mathmaster13
*/
public class OldFynotekWord extends FynotekWord {
  // Constants
  // private static final String[] digitList = {"", "ay", "fo", "us", "nos", "pur"}; May or may not be used later?

  // private static final String[] binaryList = {"po", "pura", "poña", "sola", "manta", "tauwa"}; May or may not be used later?
  
  private static HashMap<Character, String> ablautList = new HashMap<Character, String>();
  static {
    ablautList.put('a', "e");
    ablautList.put('e', "i");
    ablautList.put('i', "y");
    ablautList.put('o', "u");
    ablautList.put('u', "o");
  }

  private static HashMap<Character, Character> tenseList = new HashMap<Character, Character>();
  static {
    tenseList.put('p', '\u0000');
    tenseList.put('a', 'i');
    tenseList.put('f', 'o');
    tenseList.put('g', 'r');
  }


  // Public constructors

  /**
  Converts a String into an OldFynotekWord.
  @param word word to be converted to a OldFynotekWord.
  */
  public OldFynotekWord(String word) {
    super(word, '\u0000');
  }

  // Private constructors
  private OldFynotekWord(String a, String b, String c, char mark) {
    super(a, b, c, mark);
  }
  private OldFynotekWord(String word, char mark) {
    super(word, mark);
  }

  
  // Internal-use methods

  protected OldFynotekWord ablaut(char vowel) {
    if (vowel == '\u0000') return this;
    if (vowels.isEmpty()) return new OldFynotekWord(beginning, vowels, end, markVowel);
    String newVowels;
    if (vowel != 'r') { // 'r' is for reduplcation
      String testStr = Character.toString(vowel);
      newVowels = (vowels.equals(testStr) ? ablautList.get(vowel) : testStr);
    } else {
      int vowelLength = vowels.length();
      String temp = (vowelLength == 1 ? vowels : Character.toString(vowels.charAt(vowelLength - 1)));
      newVowels = temp + "'" + temp;
    }
    return new OldFynotekWord(beginning, newVowels, end, vowel);
  }

  protected char getNonHypoTense(char tense) {
    return tenseList.get(tense);
  }

  /* Maybe we aren't using this?
  private static String number(String seximalString, boolean isNegative) {
    if (seximalString.equals("0")) return "fui";
    String output = (isNegative ? "ñy " : "");
    for (int i = 0; i < seximalString.length(); i++) {
      int seximalDigit = seximalString.charAt(i) - 48;
      if (seximalDigit == 0) continue;
      output += (digitList[seximalDigit] + binarySuffix(seximalString.length() - i - 1) + " ");
    }
    return output.trim();
  }
  private static String binarySuffix(int num) {
    String output = "";
    for (byte i = 0; i <= 5; i++) {
      if (((num >> i) & 1) == 1) output += binaryList[i];
    }
    return output;
  } */

  
  // Public methods

  public OldFynotekWord personSuffix(int person) {
    if (person < 1 || person > 3) throw new IllegalArgumentException("person can only be a value of 1, 2, or 3");
    if (person == 1) return this;
    String suffix = (person == 2 ? "a" : "o");
    if (end.isEmpty()) suffix = "n" + suffix;
    return new OldFynotekWord(this.toString() + suffix, markVowel);
  }

  /**
  Returns whether the given sequence is phonotactically and orthographically valid in old Fynotek. Capitalization is ignored (for example, <code>"A"</code> and <code>"a"</code> are treated the same way). Multiple words can be separated by whitespace, and this function will only return <code>true</code> if all words in <code>sequence</code> are valid.  Leading and trailing whitespace is ignored. A sequence containing punctuation marks, numbers, or other non-letter characters (with the exception of <code>'</code>) returns <code>false</code>, as well as an empty sequence or one containing only whitespace.
  @param sequence the sequence to be checked for validity.
  @return <code>true</code> if <code>sequence</code> is a valid sequence, and <code>false</code> if otherwise.
  */
  public static boolean isValidSequence(String sequence) {
    return FynotekWord.isValidSequence(sequence, "[aeiouyptkmnñrfshjw'\\s]", (byte) 2, true);
  }
}
