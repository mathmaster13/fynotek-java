package com.mathmaster13.fynotek;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;

/**
 * A class for handling words in Fynotek, a conlang by mochaspen. All objects created by this class are immutable. Fynotek documentarion can be found <a href="https://aspenlangs.neocities.org/fyndoc.html">here</a>.
 * @author mathmaster13
 * @since 1.0
 */
public final class FynotekWord extends BaseFynotekWord {
    /**
     * Represents if this FynotekWord is a proper noun or not.
     * @see #_ablaut(Ablaut)
     * @see #properSuffix(Ablaut)
     * @see #match(BaseFynotekWord)
     */
    public final boolean isProper;

    // Constants
    /**
     * The irregular word <i>folo</i>, in its completely unmarked (root) form.
     * @since 2.0
     */
    @NotNull
    public static final FynotekWord FOLO = new FynotekWord("fol", "o", "", null, false);

    private static final String[] digitList = {"", "ay", "fo", "us", "nos", "pur"};

    private static final String[] binaryList = {"po", "pura", "poña", "sola", "manta", "tauwa"};

    /** Generated by IntelliJ IDEA Community. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        FynotekWord that = (FynotekWord) o;

        return isProper == that.isProper;
    }

    /** Generated by IntelliJ IDEA Community. */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (isProper ? 1 : 0);
        return result;
    }

    /**
     * The maximum integer supported by Fynotek's number system. You can compare if a number <code>x</code> is too large or small with <code>(x.abs().compareTo(MAX_MAGNITUDE) &gt; 0)</code>.
     * @see #number(BigInteger)
     */
    @NotNull
    public static final BigInteger MAX_MAGNITUDE = new BigInteger(new byte[]{43, 86, -44, -81, -113, 121, 50, 39, -116, 121, 126, -67, 0, -1, -1, -1, -1, -1, -1, -1, -1});

    private static final char[] stopList = {'p', 't', 'k'};



    // Public constructors
    /**
     * Converts a String and a boolean to a FynotekWord. The String contains the word itself, while the boolean represents whether the word is a proper noun: <code>true</code> if it is, and <code>false</code> if it is not. Leading and trailing whitespace is ignored (the <code>String.trim()</code> method is called on <code>word</code>).
     * This constructor assumes that a word is in its root form, with no inflection.
     * @param word word to be converted to a FynotekWord.
     * @param isProper <code>true</code> if this word is a proper noun, or <code>false</code> if it is not.
     */
    public FynotekWord(@NotNull String word, boolean isProper) {
        this(word, null, isProper);
    }
    /**
     * Converts a String to a FynotekWord. The word is assumed not to be a proper noun. Leading and trailing whitespace is ignored (the <code>String.trim()</code> method is called on <code>word</code>).
     * This constructor assumes that a word is in its root form, with no inflection.
     * @param word word to be converted to a FynotekWord.
     */
    public FynotekWord(@NotNull String word) {
        this(word, null, false);
    }
    /**
     * Converts a String to a FynotekWord, and marks the word as having the specified inflection.  Leading and trailing whitespace is ignored (the <code>String.trim()</code> method is called on <code>word</code>), and the word will always be converted to lowercase.
     * This constructor should be used with words known at compile time, or with words known to be marked for the present tense or the {@link Case#OTHER OTHER} case (since those forms and root forms are identical as strings).
     * For example, the accusative form of the word "hyr" is "hor", and would be created with <code>new FynotekWord("hor", Case.ACCUSATIVE, false)</code>.
     * A root form (the abstract form of a word with no case or tense marking) is represented by a <code>null</code> inflection.
     @param word word to be converted to a FynotekWord.
     @param inflection this word's inflection, or <code>null</code> if it does not have one.
     @param isProper <code>true</code> if this word is a proper noun, or <code>false</code> if it is not.
     */
    public FynotekWord(@NotNull String word, @Nullable Inflection inflection, boolean isProper) {
        super(word, inflection);
        this.isProper = isProper;
    }

    // Private constructors
    private FynotekWord(@NotNull String a, @NotNull String b, @NotNull String c, @Nullable Inflection inflection, boolean isProper) {
        super(a, b, c, inflection);
        this.isProper = isProper;
    }
    private FynotekWord(@NotNull String[] word, @Nullable Inflection inflection, boolean isProper) {
        super(word, inflection);
        this.isProper = isProper;
    }
    private FynotekWord(@NotNull BaseFynotekWord word) {
        this(word, false);
    }
    private FynotekWord(@NotNull BaseFynotekWord word, boolean isProper) {
        super(word.beginning, word.vowels, word.end, null);
        this.isProper = isProper;
    }


    // Internal-use methods
    @Override
    protected @NotNull String[] _ablaut(@NotNull Ablaut ablaut) {
        if (ablaut == Ablaut.DEFAULT || vowels.isEmpty()) return new String[]{beginning, vowels, end};
        String newVowels = vowels;
        if (ablaut == Ablaut.REDUPLICATION) {
            if (newVowels.length() == 1 || newVowels.charAt(0) != newVowels.charAt(1) ) {
                newVowels += newVowels.substring(newVowels.length() - 1);
                if (newVowels.length() > 2) {
                    newVowels = newVowels.substring(newVowels.length() - 2);
                }
            } else {
                newVowels = newVowels.substring(0, 1);
            }
        } else {
            if (vowels.charAt(vowels.length() - 1) != ablaut.asChar) {
                newVowels = (vowels.length() == 1 ? Character.toString(ablaut.asChar) : (Character.toString(vowels.charAt(0)) + ablaut.asChar));
            } else {
                newVowels += ablaut.secondary;
                if (newVowels.length() > 2) {
                    newVowels = newVowels.substring(newVowels.length()-2);
                }
            }
        }
        return new String[]{beginning, newVowels, end};
    }

    private @NotNull String[] properSuffix(@NotNull Ablaut vowel) {
        if (vowel == Ablaut.DEFAULT) return new String[]{beginning, vowels, end};
        if (vowel == Ablaut.REDUPLICATION) {
            int vowelLength = vowels.length();
            if (vowelLength == 0) return new String[]{beginning, vowels, end};
            String vowelToReduplicate = (vowelLength == 1 ? vowels : Character.toString(vowels.charAt(vowelLength - 1)));
            FynotekWord output = this.suffix(vowelToReduplicate + vowelToReduplicate);
            return new String[]{output.beginning, output.vowels, output.end};
        } else {
            String suffix = Character.toString(vowel.asChar);
            boolean addN = (end.length() == 0 && vowels.length() >= 2);
            return new String[]{addN ? beginning + vowels + "n" : beginning, addN ? suffix : vowels + suffix, end};
        }
    }

    private static boolean isStop(char letter) {
        for (int i = 0; i < 3; i++) if (letter == stopList[i]) return true;
        return false;
    }

    private static @NotNull String number(@NotNull String seximalString, boolean isNegative) {
        if (seximalString.equals("0")) return "fui";
        final StringBuilder output = new StringBuilder((isNegative ? "ñy " : ""));
        for (int i = 0; i < seximalString.length(); i++) {
            int seximalDigit = seximalString.charAt(i) - 48;
            if (seximalDigit == 0) continue;
            output.append(digitList[seximalDigit]).append(binarySuffix(seximalString.length() - i - 1)).append(" ");
        }
        return output.toString().trim();
    }
    private static @NotNull String binarySuffix(int num) {
        final StringBuilder output = new StringBuilder();
        for (byte i = 0; i <= 5; i++) {
            if (((num >> i) & 1) == 1) output.append(binaryList[i]);
        }
        return output.toString();
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
     * Returns a copy of this FynotekWord marked for the specified ablaut. For proper nouns, a suffix is applied instead.
     * If this function is used on a word, it is assumed that the word has been marked for case or tense,
     * but the case or tense is unknown.
     * This function should be called before any suffix functions, not after.
     *
     * If a word has previously been inflected, it usually should not be inflected again.
     * If this function is called on a marked word, there is no guarantee for the result.
     * Check for marking with {@link #isMarked()}.
     * @param ablaut the ablaut to mark this word as.
     * @return a copy of this FynotekWord marked for the specified ablaut.
     * @see Ablaut
     * @see #match(BaseFynotekWord)
     * @see #isMarked()
     * @since 3.0
     */
    @Override
    public @NotNull FynotekWord ablaut(@NotNull Ablaut ablaut) {
        return new FynotekWord(isProper ? properSuffix(ablaut) : _ablaut(ablaut), ablaut, isProper);
    }

    @Override
    public @Nullable Ablaut getAblaut() {
        if (this.toString().equals("folo") && inflection == Case.ACCUSATIVE) return Ablaut.DEFAULT;
        return super.getAblaut();
    }

    /**
     * Returns a copy of this FynotekWord inflected for the noun case specified by <code>caseOfNoun</code>. Note that the word "folo" as a common noun cannot be marked for the nominative case, and doing so throws an <code>IllegalArgumentException</code>.
     * This method should be called before any suffix methods, not after.
     *
     * If a word has previously been marked for case or tense, it usually should not be marked again.
     * If this method is called on a marked word, there is no guarantee for the result.
     * Check for marking with {@link #isMarked()}.
     * @param caseOfNoun the noun case to inflect this FynotekWord for.
     * @return this FynotekWord inflected for the specified noun case.
     * @see #match(BaseFynotekWord)
     * @see #isMarked()
     */
    public @NotNull FynotekWord nounCase(@NotNull Case caseOfNoun) throws IllegalArgumentException {
        if (this.toString().equals("folo") && !isProper) {
            if (caseOfNoun == Case.NOMINATIVE) throw new IllegalArgumentException("\"folo\" cannot be marked for the nominative case");
            if (caseOfNoun == Case.ACCUSATIVE) return new FynotekWord("fol", "o", "", Case.ACCUSATIVE, false);
        }
        // While there is an actual suffix function, I prefer to leave this simplified ome in for speed.
        return new FynotekWord((isProper ? this.properSuffix(caseOfNoun.ablaut) : this._ablaut(caseOfNoun.ablaut)), caseOfNoun, isProper);
    }

    /**
     * Returns a copy of this FynotekWord inflected for the verb tense (if this word is not a proper noun) or verb modifier form (if this word is a proper noun or the word <i>folo</i>) specified by <code>tenseOfVerb</code>.
     * This method should be called before any suffix methods, not after.
     *
     * If a word has previously been marked for case or tense, it usually should not be marked again.
     * If this method is called on a marked word, there is no guarantee for the result.
     * Check for marking with {@link #isMarked()}.
     * @param tenseOfVerb the verb tense to inflect this FynotekWord for.
     * @return this FynotekWord inflected for the specified verb tense.
     * @see #match(BaseFynotekWord)
     * @see BaseFynotekWord#verbTense(Tense)
     */
    @Override
    public @NotNull FynotekWord verbTense(@NotNull Tense tenseOfVerb) {
        return new FynotekWord(isProper ? properSuffix(tenseOfVerb.getAblaut()) : _ablaut(tenseOfVerb.getAblaut()), tenseOfVerb, isProper);
    }

    /**
     * Returns a new FynotekWord with the specified suffix appended to the end of this word.
     * If the suffix creates a phonotactically invalid sequence, <i>n</i> or <i>a</i> will be infixed as needed to make the resulting word phonotactically valid.
     * Leading and trailing whitespace is ignored (the <code>String.trim()</code> method is called on <code>suffix</code>).
     *
     * This method applies a suffix, so no inflections should be called on a word after this method is called on it.
     * @param suffix the suffix to be appended to the end of this FynotekWord
     * @return a FynotekWord with the specified suffix appended to the end of it.
     * @see #prefix(String)
     */
    public @NotNull FynotekWord suffix(@NotNull String suffix) {
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
        return new FynotekWord(output, inflection, isProper);
    }

    // The prefix function just calls the function method on the reverse of the input, then reverses it back.
    /**
     * Returns a new FynotekWord with the specified prefix appended to the beginning of this word.
     * If the prefix creates a phonotactically invalid sequence, <i>n</i> or <i>a</i> will be infixed as needed to make the resulting word phonotactically valid.
     * Leading and trailing whitespace is ignored (the <code>String.trim()</code> method is called on <code>prefix</code>).
     * @param prefix the prefix to be appended to the beginning of this FynotekWord.
     * @return a FynotekWord with the specified prefix appended to the beginning of it.
     * @see #suffix(String)
     */
    public @NotNull FynotekWord prefix(@NotNull String prefix) {
        if (prefix.isEmpty()) return this;
        StringBuilder temp = new StringBuilder(this.toString());
        FynotekWord reverseWord = new FynotekWord(temp.reverse().toString());
        temp = new StringBuilder(prefix);
        temp = new StringBuilder(reverseWord.suffix(temp.reverse().toString()).toString());
        return new FynotekWord(temp.reverse().toString(), inflection, isProper);
    }

    @Override
    public @NotNull FynotekWord inflect(@Nullable Inflection inflection) {
        if (inflection instanceof Case caseOfNoun) return nounCase(caseOfNoun);
        return (FynotekWord) super.inflect(inflection);
    }

    /**
     * {@inheritDoc}
     * @see #nounCase(Case)
     */
    @Override
    public @NotNull FynotekWord match(@NotNull BaseFynotekWord word) {
        if (word.inflection == null) return this;
        if (word.inflection instanceof Case caseOfNoun) return nounCase(caseOfNoun);
        if (word.inflection instanceof Tense tenseOfVerb) return verbTense(tenseOfVerb);
        return ablaut((Ablaut) word.inflection);
    }

    @Override
    public @NotNull FynotekWord personSuffix(@NotNull Person person) {
        if (person == Person.P1) return this;
        return this.suffix(person.suffix);
    }


    // Static methods
    /**
     * Returns the Fynotek translation of the specified number. If the number's absolute value is greater than <code>MAX_MAGNITUDE</code>, an empty String is returned.
     * @param num the number to be translated.
     * @return the Fynotek translation of the specified number.
     * @throws IllegalArgumentException If the number provided is too large for the number system to handle.
     * @see #MAX_MAGNITUDE
     */
    public static @NotNull String number(@NotNull BigInteger num) {
        if (num.abs().compareTo(MAX_MAGNITUDE) > 0) throw new IllegalArgumentException("Number is too large");
        return number(num.toString(6), (num.signum() == -1));
    }

    /**
     * Returns the Fynotek translation of the specified number.
     * @param num the number to be translated.
     * @return the Fynotek translation of the specified number.
     */
    public static @NotNull String number(long num) {
        return number(Long.toString(Math.abs(num), 6), (Math.signum(num) == -1));
    }


    /**
     * Returns whether the given sequence is phonotactically and orthographically valid in Fynotek.
     * Capitalization is ignored (for example, <code>"A"</code> and <code>"a"</code> are treated the same way).
     * Multiple words can be separated by whitespace, and this method will only return <code>true</code> if all words in <code>sequence</code> are valid.
     * Leading and trailing whitespace is ignored (the <code>String.trim()</code> method is called on <code>sequence</code>).
     * A sequence containing punctuation marks, numbers, or other non-letter characters returns <code>false</code>,
     * as well as an empty sequence or one containing only whitespace.
     *
     * Note that despite that "annnnn" (with any number of Ns) is used in Fynotek, it is not considered a valid sequence because it breaks phonotactics
     * (so only "an" and "ann" are valid sequences when used with this function).
     * @param sequence the sequence to be checked for validity.
     * @return <code>true</code> if <code>sequence</code> is a valid sequence, and <code>false</code> if otherwise.
     */
    // This function is a copy of `isValidSequence(String, Regex)` from the aspenlangs package,
    // in order to avoid this project having Kotlin dependencies.
    public static boolean isValidSequence(@NotNull String sequence) {
        sequence = sequence.trim().toLowerCase();
        if (isBlank(sequence)) return false;

        // Handle a multiple-word sequence
        final String[] wordArray = sequence.split("\\s+");
        if (wordArray.length == 0) return false;

        final String consonants = "([ptk](?![ptk])|[mnñrfshjwl])";
        for (String word : wordArray)
            if (!word.matches(consonants + "{0,2}[aeiouy]{1,2}(" + consonants + "{1,3}[aeiouy]{1,2})*" + consonants + "{0,2}"))
                return false;
        return true;
    }

    // Use the Kotlin definition of isBlank for consistency
    private static boolean isBlank(String s) {
        if (s.length() == 0) return true;
        for (int i = 0; i < s.length(); i++) {
            char charToCheck = s.charAt(i);
            if (!(Character.isWhitespace(charToCheck) || Character.isSpaceChar(charToCheck))) return false;
        }
        return true;
    }

    /**
     * Represents the case of a Fynotek noun.
     * @see #nounCase(Case)
     * @since 2.0
     */
    public enum Case implements SpecificInflection {
        /**
         * Represents the nominative case, marked with {@link Ablaut#DEFAULT DEFAULT} ablaut.
         * The word "folo" cannot be inflected as this case.
         *
         * @see #FOLO
         */
        NOMINATIVE(Ablaut.DEFAULT),
        /** Represents the accusative case, usually marked with {@link Ablaut#O O} ablaut. */
        ACCUSATIVE(Ablaut.O),
        /** Represents the genitive case, marked with {@link Ablaut#I I} ablaut. */
        GENITIVE(Ablaut.I),
        /** Represents the dative case, marked with {@link Ablaut#A A} ablaut. */
        DATIVE(Ablaut.A),
        /**
         * Represents any other case not given a constant here, such as prepositional cases.
         * In essence, it is the "default" case, and as such is inflected using {@link Ablaut#DEFAULT DEFAULT} ablaut.
         * Unlike the {@link #NOMINATIVE} case, "folo" can be inflected as this case.
         *
         * Note that this does <i>not</i> represent the root form of a word (the morpheme itself which is not inflected at all)—that is represented by a <code>null</code> inflection.
         */
        OTHER(Ablaut.DEFAULT);

        private final Ablaut ablaut;

        /**
         * Returns the ablaut associated with this noun case.
         * This method only gives the <i>typical</i> ablaut used (stored in a final field), and does not account for irregular words (such as "folo"). For that, use {@link BaseFynotekWord#getAblaut()}.
         * @return the ablaut associated with this noun case.
         * @see BaseFynotekWord#getAblaut()
         * @see #FOLO
         */
        @Override
        public @NotNull Ablaut getAblaut() {
            return ablaut;
        }

        Case(Ablaut ablaut) {
            this.ablaut = ablaut;
        }
    }
}
