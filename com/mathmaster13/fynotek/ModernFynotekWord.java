package com.mathmaster13.fynotek;
import java.util.HashMap;
import java.math.BigInteger;

/**
A class for handling words in Fynotek, a conlang by mochaspen. All objects created by this class are immutable. Fynotek documentarion can be found <a href="https://docs.google.com/document/d/1qb2M0042xSuhgb5d_8MYy1Ged-GpyNfHWsgZ_TZxeOQ/edit">here</a>.
@author mathmaster13
*/
public class ModernFynotekWord extends FynotekWord {
  private boolean proper;

  // Constants
  private static final String[] digitList = {"", "ay", "fo", "us", "nos", "pur"};

  private static final String[] binaryList = {"po", "pura", "poña", "sola", "manta", "tauwa"};

  /**
  The maximum integer supported by Fynotek's number system. You can compare if a number <code>x</code> is too large or small with <code>(x.abs().compareTo(MAX_MAGNITUDE) &gt; 0)</code>.
  @see #number(BigInteger)
  */
  public static final BigInteger MAX_MAGNITUDE = new BigInteger(new byte[]{43, 86, -44, -81, -113, 121, 50, 39, -116, 121, 126, -67, 0, -1, -1, -1, -1, -1, -1, -1, -1});
  
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
  

  // Public constructors

  /**
  Converts a String and a boolean into a ModernFynotekWord. The String contains the word itself, while the boolean represents whether the word is a proper noun: <code>true</code> if it is, and <code>false</code> if it is not.
  @param word word to be converted to a ModernFynotekWord.
  @param isProper whether the word is a proper noun or not.
  */
  public ModernFynotekWord(String word, boolean isProper) {
    this(word, '\u0000', isProper);
  }
  /**
  Converts a String into a ModernFynotekWord. The word is assumed not to be a proper noun.
  @param word word to be converted to a ModernFynotekWord.
  */
  public ModernFynotekWord(String word) {
    this(word, '\u0000', false);
  }

  // Private constructors
  private ModernFynotekWord(String a, String b, String c, char mark, boolean isProper) {
    super(a, b, c, mark);
    proper = isProper;
  }
  private ModernFynotekWord(String word, char mark, boolean isProper) {
    super(word, mark);
    proper = isProper;
  }
  private ModernFynotekWord(FynotekWord word) {
    super(word);
    proper = false;
  }

  
  // Internal-use methods
  /**
  {@inheritDoc}
  There is also a special case for the word <code>"folo"</code> if it is not a proper noun, since its ablaut is irregular.
  @param {@inheritDoc}
  @return {@inheritDoc}
  */
  protected ModernFynotekWord ablaut(char vowel) {
    if (vowel == '\u0000') return this;
    if (vowels.isEmpty()) return new ModernFynotekWord(beginning, vowels, end, markVowel, proper);
    if (this.toString().equals("folo") && !proper) {
      // "folo" is a special case and cannot be conjugated for nominative, so the accusative is the root form.
      if (vowel == 'a') return new ModernFynotekWord("fol", "a", "", 'a', false);
      if (vowel == 'i') return new ModernFynotekWord("fol", "i", "", 'i', false);
      else return new ModernFynotekWord("fol", "o", "", 'o', false);
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
    return new ModernFynotekWord(beginning, newVowels, end, vowel, proper);
  }

  protected char getNonHypoTense(char tense) {
    return tenseList.get(tense);
  }

  private ModernFynotekWord properSuffix(char vowel) {
    if (vowel == 'r') {
      int vowelLength = vowels.length();
      String temp = (vowelLength == 1 ? vowels : Character.toString(vowels.charAt(vowelLength - 1)));
      return this.suffix(temp + temp);
    } else {
      String suffix = Character.toString(vowel);
      if (end.length() == 0 && vowels.length() >= 2) suffix = "n" + suffix;
      return new ModernFynotekWord(this.toString() + suffix, vowel, proper);
    }
  }
  private static boolean isStop(char letter) {
    for (char i : stopList) {
      if (letter == i) return true;
    }
    return false;
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

  
  // Public methods

  /**
  Returns this ModernFynotekWord inflected for the noun case specified by <code>caseOfNoun</code>. <code>caseOfNoun</code> should be either <code>'n'</code> (nominative), <code>'a'</code> (accusative), <code>'g'</code> (genitive), or <code>'d'</code> (dative). All other charcters will cause the original object to be returned.
  @param caseOfNoun the noun case to inflect this ModernFynotekWord for.
  @return this ModernFynotekWord inflected for the specified noun case.
  @see #match(FynotekWord)
  */
  public ModernFynotekWord nounCase(char caseOfNoun) {
    if (!(caseOfNoun == 'a' || caseOfNoun == 'd' || caseOfNoun == 'g')) return this;
    char caseLetter = caseList.get(caseOfNoun);
    // While there is an actual suffix function, I prefer to leave this simplified ome in for speed.
    return (proper ? this.properSuffix(caseLetter) : this.ablaut(caseLetter));
  }

  /**
  Returns a ModernFynotekWord with the specified suffix appended to the end of this word. If the suffix creates a phonotactically invalid sequence, <i>n</i> or <i>a</i> will be infixed as needed to make the resulting word phonotactically valid.
  @param suffix the suffix to be appended to the end of this ModernFynotekWord.
  @return a ModernFynotekWord with the specified suffix appended to the end of it.
  @see #prefix(String)
  */
  public ModernFynotekWord suffix(String suffix) {
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
    return new ModernFynotekWord(output, markVowel, proper);
  }

  // The prefix function just calls the suffix function on the reverse of the input, then reverses it back.
  /**
  Returns a ModernFynotekWord with the specified prefix appended to the beginning of this word. If the prefix creates a phonotactically invalid sequence, <i>n</i> or <i>a</i> will be infixed as needed to make the resulting word phonotactically valid.
  @param prefix the prefix to be appended to the beginning of this ModernFynotekWord.
  @return a ModernFynotekWord with the specified prefix appended to the beginning of it.
  @see #suffix(String)
  */
  public ModernFynotekWord prefix(String prefix) {
    if (prefix.isEmpty()) return this;
    StringBuilder temp = new StringBuilder(this.toString());
    ModernFynotekWord reverseWord = new ModernFynotekWord(temp.reverse().toString());
    temp = new StringBuilder(prefix);
    temp = new StringBuilder(reverseWord.suffix(temp.reverse().toString()).toString());
    return new ModernFynotekWord(temp.reverse().toString(), markVowel, proper);
  }

  /**
  Returns this ModernFynotekWord inflected for the same case or tense as <code>word</code>.
  @param word the FynotekWord to match this word's inflection with.
  @return this ModernFynotekWord inflected for the same case or tense as <code>word</code>.
  @see #nounCase(char)
  @see #verbTense(char, boolean)
  */
  @Override
  public ModernFynotekWord match(FynotekWord word) {
    return (proper ? properSuffix(word.markVowel) : ablaut(word.markVowel));
  }
  
  /**
  Returns whether this ModernFynotekWord is a proper noun or not. Specifically, this returns <code>true</code> if this word is a proper noun, and <code>false</code> if it is not.
  @return whether this ModernFynotekWord is a proper noun or not.
  @see #ModernFynotekWord(String, boolean)
  */
  public boolean isProper() {
    return proper;
  }

  public ModernFynotekWord personSuffix(int person) {
    if (person < 1 || person > 3) throw new IllegalArgumentException("person can only be a value of 1, 2, or 3");
    if (person == 1) return this;
    String suffix = (person == 2 ? "a" : "o");
    return this.suffix(suffix);
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


  /**
  Returns whether the given sequence is phonotactically and orthographically valid in Fynotek. Capitalization is ignored (for example, <code>"A"</code> and <code>"a"</code> are treated the same way). Multiple words can be separated by whitespace, and this function will only return <code>true</code> if all words in <code>sequence</code> are valid.  Leading and trailing whitespace is ignored. A sequence containing punctuation marks, numbers, or other non-letter characters returns <code>false</code>, as well as an empty sequence or one containing only whitespace.
  @param sequence the sequence to be checked for validity.
  @return <code>true</code> if <code>sequence</code> is a valid sequence, and <code>false</code> if otherwise.
  */
  public static boolean isValidSequence(String sequence) {
    return FynotekWord.isValidSequence(sequence, "[aeiouyptkmnñrfshjwl\\s]", (byte) 3, false);
  }
}
