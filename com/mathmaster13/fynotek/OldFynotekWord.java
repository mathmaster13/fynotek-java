package com.mathmaster13.fynotek;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A class for handling words in an older version of Fynotek. All objects created by this class are immutable. Old Fynotek documentation can be found <a href="https://docs.google.com/document/d/1U66rWinK0Qy-xab_ifZ4KC4c95icbjSgiUk9Qc27-80/edit">here</a>.
 * @author mathmaster13
 * @since 1.0
 */
public final class OldFynotekWord extends FynotekParent {
    // Public constructors
    /**
     * Converts a String to an OldFynotekWord. Leading and trailing whitespace is ignored (the <code>String.trim()</code> method is called on <code>word</code>).
     * This constructor assumes that a word is in its root form, with no inflection.
     * @param word word to be converted to an OldFynotekWord.
     */
    public OldFynotekWord(@NotNull String word) {
        super(word, null);
    }
    /**
     * Converts a String to an OldFynotekWord, and marks the word as having the specified inflection.  Leading and trailing whitespace is ignored (the <code>String.trim()</code> method is called on <code>word</code>), and the word will always be converted to lowercase.
     * This constructor should be used with words known at compile time, or with words known to be marked for the present tense (since the present-tense form and the root form are identical as strings).
     * For example, the future-tense form of the word "ni" is "no", and would be created with <code>new OldFynotekWord("no", Tense.FUTURE)</code>.
     *
     * A root form (the abstract form of a word with no case or tense marking) is represented by a <code>null</code> inflection.
     *
     * While this constructor does not forbid you from using an instance of {@link FynotekWord.Case} as an inflection, this is not extremely useful, since Old Fynotek does not mark for case.
     * @param word word to be converted to an OldFynotekWord.
     * @param inflection this word's inflection, or <code>null</code> if it does not have one.
     */
    public OldFynotekWord(@NotNull String word, @NotNull Inflection inflection) {
        super(word, inflection);
    }

    // Private constructors
    private OldFynotekWord(@NotNull String a, @NotNull String b, @NotNull String c, @Nullable Inflection inflection) {
        super(a, b, c, inflection);
    }
    private OldFynotekWord(@NotNull String[] word, @NotNull Inflection inflection) {
        super(word, inflection);
    }
    private OldFynotekWord(@NotNull FynotekParent word) {
        super(word);
    }


    // Internal-use methods
    @Override
    protected @NotNull String[] ablaut(@NotNull Ablaut vowel) {
        if (vowel == Ablaut.DEFAULT || vowels.isEmpty()) return new String[]{beginning, vowels, end};
        if (vowel == Ablaut.REDUPLICATION) {
            int vowelLength = vowels.length();
            String vowelToReduplicate = (vowelLength == 1 ? vowels : Character.toString(vowels.charAt(vowelLength - 1)));
            return new String[]{beginning + vowelToReduplicate + "'", vowelToReduplicate, end};
        } else {
            String ablautAsString = Character.toString(vowel.asChar);
            String newVowels = (vowels.equals(ablautAsString) ?
                    (vowel == Ablaut.E ? "i" : Character.toString(vowel.secondary)) // There is a slight difference between old and modern ablaut pairs. Since Old Fynotek isn't used much, this bodged implementation will do.
                    : ablautAsString);
            return new String[]{beginning, newVowels, end};
        }
    }


    // Public methods
    /**
     * {@inheritDoc}
     * If this word was inflected with a noun case through the {@link #match(FynotekParent)} method, {@link Ablaut#DEFAULT} is returned.
     */
    @Override
    public @Nullable Ablaut getAblaut() {
        if (inflection instanceof FynotekWord.Case) return Ablaut.DEFAULT;
        if (inflection == Tense.GNOMIC) return Ablaut.REDUPLICATION;
        return super.getAblaut();
    }

    /**
     * {@inheritDoc}
     * Old Fynotek makes no distinction between hypothetical and non-hypothetical gnomic,
     * so {@link Tense#HYP_GNOMIC} and {@link Tense#GNOMIC} have the same behavior in this method.
     * However, the distinction is still made for interoperability with words from modern Fynotek.
     * If you do not have old and modern words interoperating, always pass in <code>GNOMIC</code> to ensure proper behavior of {@link #equals}.
     */
    @Override
    public @NotNull OldFynotekWord verbTense(@NotNull Tense tenseOfVerb) {
        return new OldFynotekWord(ablaut((tenseOfVerb == Tense.GNOMIC ? Tense.HYP_GNOMIC : tenseOfVerb).getAblaut()), tenseOfVerb);
    }

    /**
     * {@inheritDoc}
     * If the word to be matched with is a {@link FynotekWord} marked for a noun case, this word is returned with the FynotekWord's inflection, but with {@link Ablaut#DEFAULT} ablaut applied.
     */
    @Override
    public @NotNull OldFynotekWord match(@NotNull FynotekParent word) {
        if (word.inflection == null || word.inflection instanceof FynotekWord.Case) return new OldFynotekWord(beginning, vowels, end, word.inflection);
        return verbTense((Tense) word.inflection);
    }

    @Override
    public @NotNull OldFynotekWord personSuffix(@NotNull Person person) {
        if (person == Person.P1) return this;
        String suffix = (person == Person.P2 ? "a" : "o");
        if (end.isEmpty()) suffix = "n" + suffix;
        return new OldFynotekWord(this.toString() + suffix, inflection);
    }

    /**
     * Returns whether the given sequence is phonotactically and orthographically valid in old Fynotek. Capitalization is ignored (for example, <code>"A"</code> and <code>"a"</code> are treated the same way). Multiple words can be separated by whitespace, and this method will only return <code>true</code> if all words in <code>sequence</code> are valid.  Leading and trailing whitespace is ignored (the <code>String.trim()</code> method is called on <code>sequence</code>). A sequence containing punctuation marks, numbers, or other non-letter characters (with the exception of <code>'</code>) returns <code>false</code>, as well as an empty sequence or one containing only whitespace.
     * @param sequence the sequence to be checked for validity.
     * @return <code>true</code> if <code>sequence</code> is a valid sequence, and <code>false</code> if otherwise.
     */
    public static boolean isValidSequence(@NotNull String sequence) {
        return FynotekParent.isValidSequence(sequence, "[aeiouyptkmnñrfshjw'\\s]", (byte) 2, true);
    }
}
