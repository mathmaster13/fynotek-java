package com.mathmaster13.fynotek;
import java.util.HashMap;
import java.math.BigInteger;

/**
A class for handling words in Fynotek, a conlang by mochaspen. All objects created by this class are immutable. Fynotek documentarion can be found <a href="https://docs.google.com/document/d/1qb2M0042xSuhgb5d_8MYy1Ged-GpyNfHWsgZ_TZxeOQ/edit">here</a>.
@author mathmaster13
@since 1.0
*/
public class FynotekWord extends FynotekParent {
  private boolean proper;

  // Constants
  private static final char O_NOUN = '}'; // If 'o' is used in a noun-related thing. If it's in a verb-related thing, normal 'o' is used.
  
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
    caseList.put('a', O_NOUN);
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
  Converts a String and a boolean into a FynotekWord. The String contains the word itself, while the boolean represents whether the word is a proper noun: <code>true</code> if it is, and <code>false</code> if it is not. Leading and trailing whitespace is ignored (the <code>String.trim()</code> method is called on <code>word</code>).
  @param word word to be converted to a FynotekWord.
  @param isProper whether the word is a proper noun or not.
  */
  public FynotekWord(String word, boolean isProper) {
    this(word, '\u0000', isProper);
  }
  /**
  Converts a String into a FynotekWord. The word is assumed not to be a proper noun. Leading and trailing whitespace is ignored (the <code>String.trim()</code> method is called on <code>word</code>).
  @param word word to be converted to a FynotekWord.
  */
  public FynotekWord(String word) {
    this(word, '\u0000', false);
  }

  // Private constructors
  private FynotekWord(String a, String b, String c, char mark, boolean isProper) {
    super(a, b, c, mark);
    proper = isProper;
  }
  private FynotekWord(String word, char mark, boolean isProper) {
    super(word, mark);
    proper = isProper;
    checkForFolo();
  }
  private FynotekWord(FynotekParent word) {
    super(word);
    proper = false;
    checkForFolo();
  }
  private FynotekWord(FynotekParent word, boolean isProper) {
    super(word);
    proper = isProper;
    checkForFolo();
  }

  
  // Internal-use methods
  private void checkForFolo() {
    if (this.toString().equals("folo") && !proper) markVowel = O_NOUN;
  }
  
  protected FynotekWord ablaut(char vowel) {
    if (this.toString().equals("folo") && vowel == O_NOUN) return this;
    if (vowel == '\u0000') return this;
    if (vowels.isEmpty()) return new FynotekWord(beginning, vowels, end, markVowel, proper);
    String newVowels = vowels;
    if (vowel != 'r') { // 'r' is for reduplcation
      char convertedVowel = (vowel == O_NOUN ? 'o' : vowel);
      if (vowels.charAt(vowels.length() - 1) != convertedVowel) {
        newVowels = (vowels.length() == 1 ? Character.toString(convertedVowel) : (Character.toString(vowels.charAt(0)) + Character.toString(convertedVowel)));
      } else {
        newVowels += ablautList.get(convertedVowel);
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

  protected char getNonHypoTense(char tense) {
    return tenseList.get(tense);
  }

  private FynotekWord properSuffix(char vowel) {
    if (vowel == '\u0000') return this;
    if (vowel == 'r') {
      int vowelLength = vowels.length();
      if (vowelLength == 0) return this;
      String temp = (vowelLength == 1 ? vowels : Character.toString(vowels.charAt(vowelLength - 1)));
      return this.suffix(temp + temp);
    } else {
      String suffix = ((vowel == O_NOUN) ? "o" : Character.toString(vowel));
      if (end.length() == 0 && vowels.length() >= 2) suffix = "n" + suffix;
      return new FynotekWord(this.toString() + suffix, vowel, proper);
    }
  }

  private static boolean isStop(char letter) {
    for (int i = 0; i < 3; i++) if (letter == stopList[i]) return true;
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

  // Note: This method will be re-implemented soon to use code from isValidSequence.
  private static boolean isValidConsonantSequence(String sequence) {
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

  /**
  Returns this FynotekWord inflected for the noun case specified by <code>caseOfNoun</code>. <code>caseOfNoun</code> should be either <code>'n'</code> (nominative), <code>'a'</code> (accusative), <code>'g'</code> (genitive), or <code>'d'</code> (dative). All other charcters will cause the original object to be returned. There is also a special case for the word "folo", which is irregular.
  @param caseOfNoun the noun case to inflect this FynotekWord for.
  @return this FynotekWord inflected for the specified noun case.
  @see #match(FynotekParent)
  */
  public FynotekWord nounCase(char caseOfNoun) {
    if (!(caseOfNoun == 'a' || caseOfNoun == 'd' || caseOfNoun == 'g')) return this;
    char caseLetter = caseList.get(caseOfNoun);
    if (this.toString().equals("folo") && !proper) return (caseOfNoun == 'a' ? this : this.ablaut(caseLetter));
    // While there is an actual suffix function, I prefer to leave this simplified ome in for speed.
    return (proper ? this.properSuffix(caseLetter) : this.ablaut(caseLetter));
  }

  /**
  Returns this FynotekWord inflected for the verb tense (if this word is not a proper noun) or verb modifier form (if this word is a proper noun) specified by <code>tenseOfVerb</code>. <code>tenseOfVerb</code> should be either <code>'p'</code> (present), <code>'a'</code> (past), <code>'f'</code> (future), or <code>'g'</code> (gnomic). All other charcters will cause the original object to be returned. If <code>hypothetical</code> is set to <code>true</code>, the word will be marked for a hypothetical tense; otherwise, the word will be inflected for a non-hypothetical tense.
   @param tenseOfVerb the verb tense to inflect this FynotekWord for.
   @param hypothetical whether this word should be inflected for a hypothetical tense or not.
   @return this FynotekWord inflected for the specified verb tense.
   @see #match(FynotekParent)
   @see FynotekParent#verbTense(char, boolean)
   */
   @Override
  public FynotekWord verbTense(char tenseOfVerb, boolean hypothetical) { // 'a' is used for the past tense.
    if (!proper) return new FynotekWord(super.verbTense(tenseOfVerb, hypothetical));

    // Verb modifier forms for proper nouns
    if (!(tenseOfVerb == 'p' || tenseOfVerb == 'a' || tenseOfVerb == 'f' || tenseOfVerb == 'g')) return this;
    return (hypothetical ? properSuffix(hypoTenseList.get(tenseOfVerb)) : properSuffix(tenseList.get(tenseOfVerb)));
  }

  /**
  Returns a FynotekWord with the specified suffix appended to the end of this word. If the suffix creates a phonotactically invalid sequence, <i>n</i> or <i>a</i> will be infixed as needed to make the resulting word phonotactically valid.  Leading and trailing whitespace is ignored (the <code>String.trim()</code> method is called on <code>suffix</code>).
  @param suffix the suffix to be appended to the end of this FynotekWord.
  @return a FynotekWord with the specified suffix appended to the end of it.
  @see #prefix(String)
  */
  public FynotekWord suffix(String suffix) {
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
      // This is so complex that delegating it to (a subset of) the isValidSequence() function may be needed.
      if (!isValidConsonantSequence(vowels + end + suffix)) {
        output += ("a" + suffix);
      } else {
        output += suffix;
      }
    }
    return new FynotekWord(output, markVowel, proper);
  }

  // The prefix function just calls the suffix function on the reverse of the input, then reverses it back.
  /**
  Returns a FynotekWord with the specified prefix appended to the beginning of this word. If the prefix creates a phonotactically invalid sequence, <i>n</i> or <i>a</i> will be infixed as needed to make the resulting word phonotactically valid.  Leading and trailing whitespace is ignored (the <code>String.trim()</code> method is called in the <code>suffix</code> method).
  @param prefix the prefix to be appended to the beginning of this FynotekWord.
  @return a FynotekWord with the specified prefix appended to the beginning of it.
  @see #suffix(String)
  */
  public FynotekWord prefix(String prefix) {
    if (prefix.isEmpty()) return this;
    StringBuilder temp = new StringBuilder(this.toString());
    FynotekWord reverseWord = new FynotekWord(temp.reverse().toString());
    temp = new StringBuilder(prefix);
    temp = new StringBuilder(reverseWord.suffix(temp.reverse().toString()).toString());
    return new FynotekWord(temp.reverse().toString(), markVowel, proper);
  }

  /**
  Returns this FynotekWord inflected for the same case or tense as <code>word</code>.
  @param word the FynotekWord or OldFynotekWord to match this word's inflection with.
  @return this FynotekWord inflected for the same case or tense as <code>word</code>.
  @see #nounCase(char)
  @see #verbTense(char, boolean)
  */
  @Override
  public FynotekWord match(FynotekParent word) {
    char mark = word.markVowel;
    if (word instanceof FynotekWord) return (proper ? properSuffix(mark) : ablaut(mark));
    return new FynotekWord(super.match(word));
  }

  @Override
  public boolean isMarked() {
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

  public FynotekWord personSuffix(int person) {
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
  Returns whether the given sequence is phonotactically and orthographically valid in Fynotek. Capitalization is ignored (for example, <code>"A"</code> and <code>"a"</code> are treated the same way). Multiple words can be separated by whitespace, and this function will only return <code>true</code> if all words in <code>sequence</code> are valid.  Leading and trailing whitespace is ignored (the <code>String.trim()</code> method is called on <code>sequence</code>). A sequence containing punctuation marks, numbers, or other non-letter characters returns <code>false</code>, as well as an empty sequence or one containing only whitespace.
  @param sequence the sequence to be checked for validity.
  @return <code>true</code> if <code>sequence</code> is a valid sequence, and <code>false</code> if otherwise.
  */
  public static boolean isValidSequence(String sequence) {
    return FynotekParent.isValidSequence(sequence, "[aeiouyptkmnñrfshjwl\\s]", (byte) 3, false);
  }
}
