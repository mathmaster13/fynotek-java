package com.mathmaster13.fynotek;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A class for handling words in Fynotek, a conlang by mochaspen, in both its modern and old form.
 * The parent class of <code>FynotekWord</code> and <code>OldFynotekWord</code>, containing all shared code between the two.
 * All instances of this class' subclasses are immutable.
 *
 * All subclasses of this class allow words to be instantiated that are not phonotactically or orthographically valid in Fynotek,
 * due to names, loanwords, and other reasons. However, these classes strictly adhere to the phonotactics and orthography of Fynotek.
 * As such, each class provides a static <code>isValidSequence</code> method to check the validity of a word if desired.
 * @author mathmaster13
 * @since 1.0
 */
public abstract sealed class BaseFynotekWord permits FynotekWord, OldFynotekWord {
    /**
     * The part of a Fynotek word before its final vowel or diphthong.
     * @see #BaseFynotekWord(String)
     */
    @NotNull
    public final String beginning;
    /**
     * A Fynotek word's final vowel or diphthong.
     * @see #BaseFynotekWord(String)
     * @see #ablaut(Ablaut)
     */
    @NotNull
    public final String vowels;
    /**
     * The part of a Fynotek word after its final vowel or diphthong.
     * @see #BaseFynotekWord(String)
     */
    @NotNull
    public final String end;
    /**
     * Represents the case or tense that this word is marked with.
     * A <code>null</code> value represents a word's root form.
     *
     * Note that a root form is <i>not</i> the same as a word marked for the present tense—see {@link Inflection} for details.
     * @since 2.0
     * @see Inflection
     * @see #isMarked()
     * @see #match(BaseFynotekWord)
     * @see FynotekWord#nounCase(FynotekWord.Case)
     * @see #verbTense(Tense)
     */
    @Nullable
    public final Inflection inflection; // This class expects you to only create objects from root words, not marked forms. Create marked words with nounCase(), verbTense(), or match(), and the method will mark the word as such.

    // Constants
    private static final char[] vowelList = { 'a', 'e', 'i', 'o', 'u', 'y' };
    /**
     * A list of all stops in Fynotek, in its modern or old form. Used internally in <code>isValidSequence</code>.
     @see #isValidSequence(String, String, byte, boolean)
     */
    protected static final char[] stopList = { 'p', 't', 'k', '\'' };

    /**
     * Returns a String representation of this word.
     * @return String representation of this word.
     */
    @Override
    public @NotNull String toString() {
        return (beginning + vowels + end);
    }

    /**
     * Checks strict value-based equality of this word and an Object.
     * If two words have the same string representation but different inflections, they will not be considered equal.
     * For example, the word "tau" marked in both the hypothetical future and hypothetical gnomic tenses is "tuu", but since those two words are marked with different tenses, they are not considered equal.
     * Generated by IntellIJ IDEA Community.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseFynotekWord that = (BaseFynotekWord) o;

        if (!beginning.equals(that.beginning)) return false;
        if (!vowels.equals(that.vowels)) return false;
        if (!end.equals(that.end)) return false;
        return Objects.equals(inflection, that.inflection);
    }

    /** Generated by IntellIJ IDEA Community. */
    @Override
    public int hashCode() {
        int result = beginning.hashCode();
        result = 31 * result + vowels.hashCode();
        result = 31 * result + end.hashCode();
        result = 31 * result + (inflection != null ? inflection.hashCode() : 0);
        return result;
    }

    // Public constructors
    /**
     * Converts a String to a BaseFynotekWord. Leading and trailing whitespace is ignored (the <code>String.trim()</code> method is called on <code>word</code>).
     * This constructor assumes that a word is in its root form, with no inflection.
     * @param word word to be converted to a BaseFynotekWord.
     */
    public BaseFynotekWord(@NotNull String word) {
        this(word, null);
    }

    /**
     * Converts a String to a BaseFynotekWord, and marks the word as having the specified inflection.  Leading and trailing whitespace is ignored (the <code>String.trim()</code> method is called on <code>word</code>), and the word will always be converted to lowercase.
     * This constructor should be used with words known at compile time, or with words known to be marked for the present tense (since the present-tense form and the root form are identical as strings).
     * A root form (the abstract form of a word with no case or tense marking) is represented by a <code>null</code> inflection.
     * @param word word to be converted to a BaseFynotekWord.
     * @param inflection this word's inflection, or <code>null</code> if it does not have one.
     * @see FynotekWord#FynotekWord(String, Inflection, boolean)
     * @see OldFynotekWord#OldFynotekWord(String, Inflection)
     */
    public BaseFynotekWord(@NotNull String word, @Nullable Inflection inflection) {
        word = word.trim().toLowerCase(); // TODO: Change implementation to preserve capitalization (maybe?)
        this.inflection = inflection;
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
                    if (i > 0 && isVowel(word.charAt(i - 1))) {
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
            end = word.substring(vowelIndex + vowelLength);
        }
    }

    // Private constructors
    /**
     * Creates a BaseFynotekWord and sets its parameters to those provided.
     * @param beginning the part of a word before its final vowel or diphthong.
     * @param vowels a word's final vowel or diphthong.
     * @param end the part of a word after its final vowel or diphthong.
     * @param inflection this word's inflection, or <code>null</code> if it does not have one.
     * @see #beginning
     * @see #vowels
     * @see #end
     * @see #inflection
     */
    protected BaseFynotekWord(@NotNull String beginning, @NotNull String vowels, @NotNull String end, @Nullable Inflection inflection) {
        this.beginning = beginning;
        this.vowels = vowels;
        this.end = end;
        this.inflection = inflection;
    }

    protected BaseFynotekWord(@NotNull String[] word, @Nullable Inflection inflection) {
        this(word[0], word[1], word[2], inflection);
    }

    /**
     * Creates a new BaseFynotekWord from an existing BaseFynotekWord.
     * @param word the word to be copied to the new BaseFynotekWord.
     */
    protected BaseFynotekWord(@NotNull BaseFynotekWord word) {
        beginning = word.beginning;
        vowels = word.vowels;
        end = word.end;
        inflection = word.inflection;
    }


    // Internal-use methods
    /**
     * Marks this word with the specified ablaut.
     * @param vowel the ablaut to mark the word with.
     * @return this word marked with the specified ablaut.
     * @see #inflection
     */
    protected abstract @NotNull String[] ablaut(@NotNull Ablaut vowel);

    /**
     * Checks if a given character is a vowel. Specifically,returns <code>true</code> if and only if the given character is <code>'a'</code>, <code>'e'</code>, <code>'i'</code>, <code>'o'</code>, <code>'u'</code>, or <code>'y'</code>.
     * @param letter the character to be checked for whether it is a vowel or not.
     * @return <code>true</code> if and only if <code>letter</code> is a vowel.
     */
    protected static boolean isVowel(char letter) {
        for (char i : vowelList) if (letter == i) return true;
        return false;
    }

    private static boolean isStop_internal(char letter) {
        for (char i : stopList) {
            if (letter == i)
                return true;
        }
        return false;
    }

    /**
     * A back-end for the <code>isValidSequence</code> functions in this class' subclasses. As such, implementation details will not be provided.
     * @return <code>true</code> if <code>sequence</code> is a valid sequence, and <code>false</code> if not.
     * @see OldFynotekWord#isValidSequence(String)
     * @see FynotekWord#isValidSequence(String)
     */
    protected static boolean isValidSequence(@NotNull String sequence, @NotNull String regex, byte maxConsonants, boolean checkForSameConsonants) { // If stuff goes wrong try changing the byte to an int. Also, !checkForSameConsonants is used to check for if you should apply the special end cases for consonants.
        sequence = sequence.toLowerCase().trim();

        // Blank string check
        if (sequence.isEmpty())
            return false;

        // Orthographic validity check
        if (!sequence.replaceAll(regex, "").isEmpty())
            return false;

        // Check for a multiple-word sequence
        final String[] wordArray = sequence.split("\\s+");
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
     * Returns the {@link Ablaut} that this word is marked with, or <code>null</code> if it is completely unmarked (a root form).
     * @return the Ablaut that this word is marked with
     * @see Ablaut
     * @since 2.0
     */
    public @Nullable Ablaut getAblaut() {
        if (inflection == null) return null;
        return inflection.getAblaut();
    }

    /**
     * Returns a copy of this word inflected for the verb tense specified by <code>tenseOfVerb</code>.
     * This function should be called before any suffix functions, not after.
     *
     * If a word has previously been marked for case or tense, it usually should not be marked again.
     * If this function is called on a marked word, there is no guarantee for the result.
     * Check for marking with {@link #isMarked()}.
     * @param tense the verb tense to inflect this BaseFynotekWord for.
     * @return this BaseFynotekWord inflected for the specified verb tense.
     * @see #match(BaseFynotekWord)
     * @see #isMarked()
     */
    public abstract @NotNull BaseFynotekWord verbTense(@NotNull Tense tense);

    /**
     * Returns a copy of this word inflected for the same case or tense as <code>word</code>.
     * This function should be called before any suffix functions, not after.
     * If a word has previously been marked for case or tense, it usually should not be marked again.
     * If this function is called on a marked word, a warning will be generated, and there is no guarantee for the result.
     * @param word the BaseFynotekWord to match this word's inflection with.
     * @return this word inflected for the same case or tense as <code>word</code>.
     * @see #verbTense(Tense)
     */
    public abstract @NotNull BaseFynotekWord match(@NotNull BaseFynotekWord word);

    /**
     * Returns whether this BaseFynotekWord is marked or not. Specifically, returns <code>inflection != null</code>.
     * @return <code>true</code> if this BaseFynotekWord has been marked by ablaut or a proper noun suffix, and <code>false</code> if it has not been.
     * @see #inflection
     */
    public final boolean isMarked() {
        return (inflection != null);
    }

    /**
     * Returns a copy of this word with a suffix added to mark the first, second, or third person.
     *
     * This method applies a suffix, so no inflections should be called on a word after this method is called on it.
     * @param person the person to mark this word for.
     * @return this word with a suffix added to mark the first, second, or third person.
     * @see #verbTense(Tense)
     */
    public @NotNull abstract BaseFynotekWord personSuffix(@NotNull Person person);

    /**
     * Represents Fynotek ablaut. If a field is not applicable to a particular form of ablaut, a null character (<code>\u0000</code>) is used.
     * @since 2.0
     */
    public enum Ablaut {
        /**
         * Represents a word that has been inflected for a case or tense, but the inflection is an "implied" form of a word.
         * Nominative-case nouns and present-tense verbs fall into this category.
         *
         * Note that this does <i>not</i> represent the root form of a word (the morpheme itself which is not inflected at all)—that is represented by a <code>null</code> ablaut.
         */
        DEFAULT('\u0000', '\u0000'),
        /** Represents A ablaut. The secondary letter for A ablaut is E. */
        A('a', 'e'),
        /** Represents E ablaut. The secondary letter for E ablaut is A (in modern Fynotek) or I (in old Fynotek). */
        E('e', 'a'),
        /** Represents I ablaut. The secondary letter for I ablaut is Y. */
        I('i', 'y'),
        /** Represents Y ablaut. The secondary letter for Y ablaut is I. */
        Y('y', 'i'),
        /** Represents O ablaut. The secondary letter for O ablaut is U. */
        O('o', 'u'),
        /** Represents U ablaut. The secondary letter for U ablaut is O. */
        U('u', 'o'),
        /** Represents reduplication ablaut. This lengthens a word's final vowel if it is short, and shortens it if it is long. */
        REDUPLICATION('\u0000', '\u0000');

        /**
         * The (primary) letter of this ablaut as a <code>char</code>.
         *
         * {@link #DEFAULT} and {@link #REDUPLICATION} have no letter association, so this field is a null character (<code>'\u0000'</code> for them.
         */
        public final char asChar;
        /**
         * The letter paired with this ablaut as a <code>char</code>.
         * This field stores the letter to be used if the last vowel of a word is already the ablaut's primary letter (shown as its enum constant name).
         * That is, if applying a primary letter to a root form would have no effect, this letter will be used instead.
         *
         * {@link #DEFAULT} and {@link #REDUPLICATION} have no letter association, so this field is a null character (<code>'\u0000'</code> for them.
         *
         * The character stored in this field is biased towards modern Fynotek, so {@link #E} has <code>'a'</code> in this field and <i>not</i> <code>'i'</code>.
         */
        public final char secondary;

        Ablaut(char asChar, char secondary) {
            this.asChar = asChar;
            this.secondary = secondary;
        }
    }

    /**
     * Represents the tense of a Fynotek verb. Tenses prefixed with <code>HYP_</code> are hypothetical tenses.
     * @since 2.0
     */
    public enum Tense implements Inflection {
        /**
         * Represents the present tense, marked with {@link Ablaut#DEFAULT DEFAULT} ablaut.
         *
         * Note that this does <i>not</i> represent the root form of a word (the morpheme itself which is not inflected at all)—that is represented by a <code>null</code> inflection.
         */
        PRESENT(Ablaut.DEFAULT),
        /** Represents the past tense, marked with {@link Ablaut#I I} ablaut. */
        PAST(Ablaut.I),
        /** Represents the future tense, marked with {@link Ablaut#O O} ablaut. */
        FUTURE(Ablaut.O),
        /**
         * Represents the gnomic tense, marked with {@link Ablaut#Y Y} ablaut in modern Fynotek and {@link Ablaut#REDUPLICATION REDUPLICATION} ablaut in old Fynotek.
         *
         * Old Fynotek makes no distinction between gnomic and hypothetical gnomic tenses (they are both simply referred to as "gnomic"),
         * so unless there is interoperation between old and modern Fynotek words, this constant should always be used over {@link #HYP_GNOMIC} in old Fynotek.
         */
        GNOMIC(Ablaut.Y),
        /** Represents the hypothetical present tense, marked with {@link Ablaut#A A} ablaut. */
        HYP_PRESENT(Ablaut.A),
        /** Represents the hypothetical past tense, marked with {@link Ablaut#E E} ablaut. */
        HYP_PAST(Ablaut.E),
        /** Represents the hypothetical future tense, marked with {@link Ablaut#U U} ablaut. */
        HYP_FUTURE(Ablaut.U),
        /**
         * Represents the hypothetical gnomic tense, marked with {@link Ablaut#REDUPLICATION REDUPLICATION} ablaut.
         *
         * Old Fynotek makes no distinction between gnomic and hypothetical gnomic tenses (they are both simply referred to as "gnomic"),
         * so unless there is interoperation between old and modern Fynotek words, {@link #GNOMIC} should always be used over this constant in old Fynotek.
         */
        HYP_GNOMIC(Ablaut.REDUPLICATION);

        @NotNull
        private final Ablaut ablaut;

        /**
         * Returns the ablaut associated with this verb tense.
         * This method only gives the <i>typical</i> ablaut used (stored in a final field), and does not account for irregular words. For that, use {@link BaseFynotekWord#getAblaut()}.
         *
         * The ablaut returned by this method is biased towards modern Fynotek, so {@link #GNOMIC} returns {@link Ablaut#Y} and <i>not</i> {@link Ablaut#REDUPLICATION}.
         * @return the ablaut associated with this noun case.
         * @see BaseFynotekWord#getAblaut()
         */
        @Override
        public @NotNull Ablaut getAblaut() {
            return ablaut;
        }

        Tense(@NotNull Ablaut ablaut) {
            this.ablaut = ablaut;
        }
    }

    /**
     * Represents the concept of person on a Fynotek verb.
     * @see #personSuffix(Person)
     * @since 2.0
     */
    public enum Person {
        /** Represents a first-person verb form. No suffix is applied for first-person verbs. */
        P1(null),
        /** Represents a second-person verb form. Second-person verbs use an "a" suffix. */
        P2("a"),
        /** Represents a third-person verb form. Third-person verbs use an "o" suffix. */
        P3("o");

        /** Represents the suffix corresponding to this person. */
        @Nullable
        public final String suffix;

        Person(@Nullable String suffix) {
            this.suffix = suffix;
        }
    }
}