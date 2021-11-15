package com.mathmaster13.fynotek;

import java.util.HashMap;

/**
A class for handling words in Fynotek, a conlang by mochaspen, in both its modern and old form.

@author mathmaster13
*/
public abstract class FynotekWord {
  /** The part of a FynotekWord before its final vowel or diphthong.
  @see #FynotekWord(String, char)
  */
  protected String beginning;
  /** A FynotekWord's final vowel or diphthong.
  @see #FynotekWord(String, char)
  @see #ablaut(char)
  */
  protected String vowels;
  /** The part of a FynotekWord after its final vowel or diphthong.
  @see #FynotekWord(String, char)
  */
  protected String end;
  /** The vowel that a FynotekWord was marked with through ablaut. A non-marked word uses the value <code>'\u0000'</code>, and a word marked by reduplication ablaut uses <code>'r'</code>. This field should not be modified by a programmer unless they make a different implementation of the <code>match</code> method.
  @see #isMarked()
  @see #match(FynotekWord)
  @see #ablaut(char)
  */
  protected char markVowel; // This class expects you to only create objects from root words, not marked forms. Create marked words with nounCase(), verbTense(), or match(), and the method will mark the word as such.

  // Constants
  private static final char[] vowelList = { 'a', 'e', 'i', 'o', 'u', 'y' };
  private static final char[] internalStopList = { 'p', 't', 'k', '\'' };

  private static HashMap<Character, Character> hypoTenseList = new HashMap<Character, Character>(); // hypothetical                                                                      tenses
  static {
    hypoTenseList.put('p', 'a');
    hypoTenseList.put('a', 'e');
    hypoTenseList.put('f', 'u');
    hypoTenseList.put('g', 'r');
  }

  /**
  Returns a String representation of this FynotekWord.
  @return String representation of this FynotekWord.
  */
  @Override
  public String toString() {
    return (beginning + vowels + end);
  }

  // Public constructors
  /**
  Converts a String into a FynotekWord.
  @param word word to be converted to a FynotekWord.
  */
  public FynotekWord(String word) {
    this(word, '\u0000');
  }

  // Private constructors
  /**
  Creates a FynotekWord and sets its parameters to those provided.
  @param a the part of a word before its final vowel or diphthong.
  @param b a word's final vowel or diphthong.
  @param c the part of a word after its final vowel or diphthong.
  @param mark the character to set <code>markVowel</code> to.
  @see #beginning
  @see #vowels
  @see #end
  @see #markVowel
  */
  protected FynotekWord(String a, String b, String c, char mark) {
    beginning = a;
    vowels = b;
    end = c;
    markVowel = mark;
  }

  /**
  Creates a new FynotekWord from an existing FynotekWord.
  @param word the word to be copied to the new FynotekWord.
  */
  protected FynotekWord(FynotekWord word) {
    beginning = word.beginning;
    vowels = word.vowels;
    end = word.end;
    markVowel = word.markVowel;
  }

  /**
  Converts a String into a FynotekWord, and marks the word as having the specified ablaut.
  @param word word to be converted to a FynotekWord.
  @param mark the type of ablaut this word has.
  */
  protected FynotekWord(String word, char mark) {
    markVowel = mark;
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
  /**
  Marks this FynotekWord with the specified ablaut.
  @param vowel the ablaut to mark the word as.
  @return this FynotekWord marked with the specified ablaut.
  @see #markVowel
  */
  protected abstract FynotekWord ablaut(char vowel);

  /**
  An internal-use method used in <code>verbTense</code>. It is not recommended to use this method when extending this class. As such, documentation will not be provided. When extending this class, one can simply make this method return <code>'\u0000'</code> or something similar.
  */
  protected abstract char getNonHypoTense(char tense);

  /**
  Checks if a given character is a vowel. Specifically, returns <code>true</code> if and only if the given character is <code>'a'</code>, <code>'e'</code>, <code>'i'</code>, <code>'o'</code>, <code>'u'</code>, or <code>'y'</code>.
  @param letter the character to be checked for whether it is a vowel or not.
  @return <code>true</code> if and only if <code>letter</code> is a vowel.
  */
  protected static boolean isVowel(char letter) {
    for (char i : vowelList) {
      if (letter == i)
        return true;
    }
    return false;
  }

  private static boolean isStop_internal(char letter) {
    for (char i : internalStopList) {
      if (letter == i)
        return true;
    }
    return false;
  }

  /**
  A back-end for the <code>isValidSequence</code> functions in this class' subclasses. Not recommended to be used if a programmer extends this class. As such, implementation details will not be provided.
  @return <code>true</code> if <code>sequence</code> is a valid sequence, and <code>false</code> if not.
  @see OldFynotekWord#isValidSequence(String)
  @see ModernFynotekWord#isValidSequence(String)
  */
  protected static boolean isValidSequence(String sequence, String regex, byte maxConsonants, boolean checkForSameConsonants) { // If stuff goes wrong try changing the byte to an int. Also, !checkForSameConsonants is used to check for if you should apply the special end cases for consonants.
    sequence = sequence.toLowerCase().trim();
    // Blank string check
    if (sequence.isEmpty())
      return false;

    // Orthographic validity check
    if (!sequence.replaceAll(regex, "").isEmpty())
      return false;

    // Check for a multiple-word sequence
    String[] wordArray = sequence.split("\\s+");
    if (wordArray.length == 0)
      return false;
    if (wordArray.length > 1) {
      boolean output = true;
      for (String j : wordArray)
        output = (isValidSequence(j, regex, maxConsonants, checkForSameConsonants) && output);
      return output;
    }

    // Phonotactic vallidity check
    int i = 0;
    while (i < sequence.length()) {
      char testChar = sequence.charAt(i);
      if (isVowel(testChar)) {
        if (i > sequence.length() - 3)
          return true; // If we get to this point, no VVV can occur.
        // VVV check
        int j;
        for (j = i + 1; j < sequence.length(); j++)
          if (!isVowel(sequence.charAt(j)))
            break;
        if (j - i > 2)
          return false;
        i = j;

      } else {
        // Consonant check
        int j;
        for (j = i + 1; j < sequence.length(); j++)
          if (isVowel(sequence.charAt(j)))
            break;
        if (j - i + (!checkForSameConsonants && (i == 0 || j == sequence.length()) ? 1 : 0) > maxConsonants)
          return false;

        // Same character check, if applicable
        if (checkForSameConsonants) {
          char checkForSameChar = testChar;
          for (int k = i + 1; k < j; k++) {
            char c = sequence.charAt(k);
            if (c == checkForSameChar)
              return false;
            checkForSameChar = c;
          }
        }

        // Stop+Stop check
        boolean stopCheck = isStop_internal(testChar);
        for (int l = i + 1; l < j; l++) {
          boolean currentCharIsStop = isStop_internal(sequence.charAt(l));
          if (stopCheck && currentCharIsStop)
            return false;
          stopCheck = currentCharIsStop;
        }

        i = j;
      }
    }
    return true;
  }

  // Public methods

  /**
  Returns this FynotekWord inflected for the verb tense specified by <code>tenseOfVerb</code>. <code>tenseOfVerb</code> should be either <code>'p'</code> (present), <code>'a'</code> (past), <code>'f'</code> (future), or <code>'g'</code> (gnomic). All other charcters will cause the original object to be returned. If <code>hypothetical</code> is set to <code>true</code>, the word will be marked for a hypothetical tense; otherwise, the word will be inflected for a non-hypothetical tense. If the word is an <code>OldFynotekWord</code>, <code>verbTense('g', false)</code> and <code>verbTense('g', true)</code> will always return the same result. It is recommended to override this implementation if a programmer extends this class.
   @param tenseOfVerb  the verb tense to inflect this FynotekWord for.
   @param hypothetical whether this word should be inflected for a hypothetical tense or not.
   @return this FynotekWord inflected for the specified verb tense.
   @see #match(FynotekWord)
   */
  public FynotekWord verbTense(char tenseOfVerb, boolean hypothetical) { // 'a' is used for the past tense.
    if (!(tenseOfVerb == 'p' || tenseOfVerb == 'a' || tenseOfVerb == 'f' || tenseOfVerb == 'g'))
      return this;
    return (hypothetical ? ablaut(hypoTenseList.get(tenseOfVerb)) : ablaut(getNonHypoTense(tenseOfVerb))); // If the tense is hypothetical, the old and new Fynotek forms are the same, so we don't need a polymorphic getter.
  }

  /**
  Returns this FynotekWord inflected for the non-hypothetical verb tense specified by <code>tenseOfVerb</code>. <code>tenseOfVerb</code> should be either <code>'p'</code> (present), <code>'a'</code> (past), <code>'f'</code> (future), or <code>'g'</code> (gnomic). All other charcters will cause the original object to be returned.
  @param tenseOfVerb the verb tense to inflect this FynotekWord for.
  @return this FynotekWord inflected for the specified verb tense.
  @see #match(FynotekWord)
   */
  public FynotekWord verbTense(char tenseOfVerb) {
    return verbTense(tenseOfVerb, false);
  }

  /**
  Returns this FynotekWord inflected for the same case or tense as <code>word</code>. It is recommended to override this implementation if a programmer extends this class.
  @param word the FynotekWord to match this word's inflection with.
  @return this FynotekWord inflected for the same case or tense as <code>word</code>.
  @see #verbTense(char, boolean)
  */
  public FynotekWord match(FynotekWord word) {
    return ablaut(word.markVowel);
  }

  /**
  Returns whether this FynotekWord is marked or not. Specifically, returns <code>(markVowel != '\u0000')</code>.
  @return <code>true</code> if this FynotekWord is marked for case or tense, and <code>false</code> if not.
  @see #markVowel
  */
  public boolean isMarked() {
    return (markVowel != '\u0000');
  }

  /**
  Returns this FynotekWord with a suffix added to mark the first, second, or third person.
  @param person the person to mark this word for. Can only be <code>1</code>, <code>2</code>, or <code>3</code>.
  @return this FynotekWord with a suffix added to mark the first, second, or third person.
  @throws IllegalArgumentException if the person specified is a value other than <code>1</code>, <code>2</code>, or <code>3</code>.
  @see #verbTense(char, boolean)
  */
  public abstract FynotekWord personSuffix(int person);
}