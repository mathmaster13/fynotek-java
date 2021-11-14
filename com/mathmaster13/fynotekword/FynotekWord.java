package com.mathmaster13.fynotekword;
import java.util.HashMap;
import java.math.BigInteger;

/**
A class for handling words in Fynotek, a conlang by mochaspen.
@author mathmaster13
*/
public class FynotekWord {
  private String beginning;
  private String vowels;
  private String end;
  private boolean proper;
  private char markVowel; // This class expects you to only create objects from root words, not ablauted forms. Create ablauted words with ablaut(), and the method will mark the word as such.

  // Constants
  private static final String[] digitList = {"", "ay", "fo", "us", "nos", "pur"};

  private static final String[] binaryList = {"po", "pura", "poña", "sola", "manta", "tauwa"};

  /**
  The maximum integer supported by Fynotek's number system. You can compare if a number <code>x</code> is too large or small with <code>(x.abs().compareTo(MAX_MAGNITUDE) &gt; 0)</code>.
  @see #number(BigInteger)
  */
  public static final BigInteger MAX_MAGNITUDE = new BigInteger(new byte[]{43, 86, -44, -81, -113, 121, 50, 39, -116, 121, 126, -67, 0, -1, -1, -1, -1, -1, -1, -1, -1});
  
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

  
  /**
  Returns a String representation of this FynotekWord. The String returned only contains the actual word, and not any other data (such as whether the word is proper or not).
  @return String representation of this FynotekWord.
  */
  @Override
  public String toString() {
    return (beginning + vowels + end);
  }
  

  // Public constructors

  /**
  Converts a String and a boolean into a FynotekWord. The String contains the word itself, while the boolean represents whether the word is a proper noun: <code>true</code> if it is, and <code>false</code> if it is not.
  @param word word to be converted to a FynotekWord.
  @param isProper whether the word is a proper noun or not.
  */
  public FynotekWord(String word, boolean isProper) {
    this(word, '\u0000', isProper);
  }
  /**
  Converts a String into a FynotekWord. The word is assumed not to be a proper noun.
  @param word word to be converted to a FynotekWord.
  */
  public FynotekWord(String word) {
    this(word, '\u0000', false);
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

  /**
  Returns this FynotekWord inflected for the noun case specified by <code>caseOfNoun</code>. <code>caseOfNoun</code> should be either <code>'n'</code> (nominative), <code>'a'</code> (accusative), <code>'g'</code> (genitive), or <code>'d'</code> (dative). All other charcters will cause the original object to be returned.
  @param caseOfNoun the noun case to inflect this FynotekWord for.
  @return this FynotekWord inflected for the specified noun case.
  */
  public FynotekWord nounCase(char caseOfNoun) {
    if (!(caseOfNoun == 'a' || caseOfNoun == 'd' || caseOfNoun == 'g')) return this;
    char caseLetter = caseList.get(caseOfNoun);
    // While there is an actual suffix function, I prefer to leave this simplified ome in for speed.
    return (proper ? this.properSuffix(caseLetter) : this.ablaut(caseLetter));
  }

  /**
  Returns this FynotekWord inflected for the verb tense specified by <code>tenseOfVerb</code>. <code>tenseOfVerb</code> should be either <code>'p'</code> (present), <code>'a'</code> (past), <code>'f'</code> (future), or <code>'g'</code> (gnomic). All other charcters will cause the original object to be returned. If <code>hypothetical</code> is set to <code>true</code>, the word will be marked for a hypothetical tense; otherwise, the word will be inflected for a non-hypothetical tense.
  @param tenseOfVerb the verb tense to inflect this FynotekWord for.
  @param hypothetical whether this word should be inflected for a hypothetical tense or not.
  @return this FynotekWord inflected for the specified verb tense.
  */
  public FynotekWord verbTense(char tenseOfVerb, boolean hypothetical) { // 'a' is used for the past tense.
    if (!(tenseOfVerb == 'p' || tenseOfVerb == 'a' || tenseOfVerb == 'f' || tenseOfVerb == 'g')) return this;
    return (hypothetical ? this.ablaut(hypoTenseList.get(tenseOfVerb)) : this.ablaut(tenseList.get(tenseOfVerb)));
  }
  /**
  Returns this FynotekWord inflected for the non-hypothetical verb tense specified by <code>tenseOfVerb</code>. <code>tenseOfVerb</code> should be either <code>'p'</code> (present), <code>'a'</code> (past), <code>'f'</code> (future), or <code>'g'</code> (gnomic). All other charcters will cause the original object to be returned.
  @param tenseOfVerb the verb tense to inflect this FynotekWord for.
  @return this FynotekWord inflected for the specified verb tense.
  */
  public FynotekWord verbTense(char tenseOfVerb) {
    return this.verbTense(tenseOfVerb, false);
  }

  /**
  Returns a FynotekWord with the specified suffix appended to the end of this word. If the suffix creates a phonotactically invalid sequence, <i>n</i> or <i>a</i> will be infixed as needed to make the resulting word phonotactically valid.
  @param suffix the suffix to be appended to the end of this FynotekWord.
  @return a FynotekWord with the specified suffix appended to the end of it.
  @see #prefix(String)
  */
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
  /**
  Returns a FynotekWord with the specified prefix appended to the beginning of this word. If the prefix creates a phonotactically invalid sequence, <i>n</i> or <i>a</i> will be infixed as needed to make the resulting word phonotactically valid.
  @param prefix the prefix to be appended to the beginning of this FynotekWord.
  @return a FynotekWord with the specified prefix appended to the beginning of it.
  @see #suffix(String)
  */
  public FynotekWord prefix(String prefix) {
    StringBuilder temp = new StringBuilder(this.toString());
    FynotekWord reverseWord = new FynotekWord(temp.reverse().toString());
    temp = new StringBuilder(prefix);
    temp = new StringBuilder(reverseWord.suffix(temp.reverse().toString()).toString());
    return new FynotekWord(temp.reverse().toString(), markVowel, proper);
  }

  /**
  Returns this FynotekWord inflected for the same case or tense as <code>word</code>.
  @param word the FynotekWord to match this word's inflection with.
  @return this FynotekWord inflected for the same case or tense as <code>word</code>.
  @see #nounCase(char)
  @see #verbTense(char, boolean)
  */
  public FynotekWord match(FynotekWord word) {
    return (word.proper ? this.properSuffix(word.markVowel) : this.ablaut(word.markVowel));
  }

  /**
  Returns whether this FynotekWord has been marked previously or not. To be specific, this function returns <code>true</code> if and only if this word was created as an output from the <code>nounCase</code> or <code>verbTense</code> method. You can use this method to make sure a word does not get marked more than once.
  @return a FynotekWord with the specified suffix appended to the end of it.
  @see #nounCase(char)
  @see #verbTense(char, boolean)
  */
  public boolean marked() {
    return (markVowel != '\u0000');
    }
  
  /**
  Returns whether this FynotekWord is a proper noun or not. Specifically, this returns <code>true</code> if this word is a proper noun, and <code>false</code> if it is not.
  @return whether this FynotekWord is a proper noun or not.
  @see #FynotekWord(String, boolean)
  */
  public boolean isProper() {
    return proper;
  }

  
  // Static methods

  /**
  Returns the Fynotek translation of the specified number. If the number's absolute value is greater than <code>MAX_MAGNITUDE</code>, an empty String is returned.
  @param num the number to be translated.
  @return the Fynotek translation of the specified number.
  @throws IllegalArgumentException If the number provided is too large for the number system to handle.
  @see #MAX_MAGNITUDE
  */
  public static String number(BigInteger num) {
    if (num.abs().compareTo(MAX_MAGNITUDE) > 0) throw new IllegalArgumentException("Number is too large");
    return number(num.toString(6), (num.signum() == -1));
  }

  /**
  Returns the Fynotek translation of the specified number.
  @param num the number to be translated.
  @return the Fynotek translation of the specified number.
  */
  public static String number(long num) {
    return number(Long.toString(Math.abs(num), 6), (Math.signum(num) == -1));
  }
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
  }

  /**
  Returns whether the given sequence is phonotactically and orthographically valid in Fynotek. Multiple words can be separated by whitespace, and this function will only return <code>true</code> if all words in <code>sequence</code> are valid.  Leading and trailing whitespace is ignored. A sequence containing punctuation marks, numbers, or other non-letter characters returns <code>false</code>, as well as an empty sequence or one containing only whitespace.
  @param sequence the sequence to be checked for validity.
  @return <code>true</code> if <code>sequence</code> is a valid sequence, and <code>false</code> if otherwise.
  */
  public static boolean isValidSequence(String sequence) {
    sequence = sequence.toLowerCase().trim();
    // Blank string check
    if (sequence.equals("")) return false;
    
    // Orthographic validity check
    if (!sequence.replaceAll("[aeiouyptkmnñrfshjwl\\s]", "").equals("")) return false;

    // Check for a multiple-word sequence
    String[] wordArray = sequence.split("\\s+");
    if (wordArray.length == 0) return false;
    if (wordArray.length > 1) {
      boolean output = true;
      for (String j : wordArray) output = (isValidSequence(j) && output);
      return output;
    }
    
    // Phonotactic vallidity check
    int i = 0;
    while (i < sequence.length()) {
      char testChar = sequence.charAt(i);
      if (isVowel(testChar)) {
        if (i > sequence.length() - 3) return true; // If we get to this point, no VVV can occur.
        // VVV check
        int j;
        for (j = i + 1; j < sequence.length(); j++) if (!isVowel(sequence.charAt(j))) break;
        if (j - i > 2) return false;
        i = j;
        
      } else {
        // Stop+Stop check
        int j;
        for (j = i; j < sequence.length(); j++) if (!isStop(sequence.charAt(j))) break;
        int stopAmount = j - i;
        if (stopAmount > 1) return false;
        
        // CCCC check (CCC at beginning or end)
        int k;
        for (k = j; k < sequence.length(); k++) if (isVowel(sequence.charAt(k))) break;
        if (k - j + stopAmount + (i == 0 || k == sequence.length() ? 1 : 0) > 3) return false;
        i = k;
      }
    }
    return true;
  }
}
