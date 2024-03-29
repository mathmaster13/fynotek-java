package io.github.mathmaster13.aspenlangs.fynotek;
import javax.annotation.Nullable;

/**
 * A class for handling words in an older version of Fynotek. All objects created by this class are immutable. Old Fynotek documentation can be found <a href="https://docs.google.com/document/d/1U66rWinK0Qy-xab_ifZ4KC4c95icbjSgiUk9Qc27-80/edit">here</a>.
 * @author mathmaster13
 * @since 1.0
 */
public final class OldFynotekWord extends BaseFynotekWord {
    // Public constructors
    /**
     * Converts a String to an OldFynotekWord. Leading and trailing whitespace is ignored (the <code>String.trim()</code> method is called on <code>word</code>).
     * This constructor assumes that a word is in its root form, with no inflection.
     * @param word word to be converted to an OldFynotekWord.
     */
    public OldFynotekWord(String word) {
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
    public OldFynotekWord(String word, Inflection inflection) {
        super(word, inflection);
    }

    // Private constructors
    private OldFynotekWord(String a, String b, String c, @Nullable Inflection inflection) {
        super(a, b, c, inflection);
    }
    private OldFynotekWord(String[] word, Inflection inflection) {
        super(word, inflection);
    }
    private OldFynotekWord(BaseFynotekWord word) {
        super(word);
    }


    // Internal-use methods
    @Override
    protected String[] _ablaut(Ablaut ablaut) {
        if (ablaut == Ablaut.Y) throw new IllegalArgumentException("Y ablaut is undefined in Old Fynotek");
        if (ablaut == Ablaut.DEFAULT || vowels.isEmpty()) return new String[]{beginning, vowels, end};
        if (ablaut == Ablaut.REDUPLICATION) {
            int vowelLength = vowels.length();
            String vowelToReduplicate = (vowelLength == 1 ? vowels : Character.toString(vowels.charAt(vowelLength - 1)));
            return new String[]{beginning + vowelToReduplicate + "'", vowelToReduplicate, end};
        } else {
            String ablautAsString = Character.toString(ablaut.asChar);
            String newVowels = (vowels.equals(ablautAsString) ?
                    (ablaut == Ablaut.E ? "i" : Character.toString(ablaut.secondary)) // There is a slight difference between old and modern ablaut pairs. Since Old Fynotek isn't used much, this bodged implementation will do.
                    : ablautAsString);
            return new String[]{beginning, newVowels, end};
        }
    }


    // Public methods
    /**
     * Returns a copy of this OldFynotekWord marked for the specified ablaut.
     * If {@link Ablaut#Y Ablaut.Y} is entered as the ablaut to mark the word as,
     * an {@code IllegalArgumentException} will be thrown, since there is no defined Y ablaut in Old Fynotek.
     * If this function is used on a word, it is assumed that the word has been marked for case or tense,
     * but the case or tense is unknown.
     * Since Old Fynotek has no case-marking, this function's only use is when doing interoperation with modern Fynotek words.
     * This function should be called before any suffix functions, not after.
     *
     * If a word has previously been inflected, it usually should not be inflected again.
     * If this function is called on a marked word, there is no guarantee for the result.
     * Check for marking with {@link #isMarked()}.
     * @param ablaut the ablaut to mark this word as.
     * @return a copy of this OldFynotekWord marked for the specified ablaut.
     * @see Ablaut
     * @see #match(BaseFynotekWord)
     * @see #isMarked()
     * @since 3.0
     */
    @Override
    public OldFynotekWord ablaut(Ablaut ablaut) throws IllegalArgumentException {
        return new OldFynotekWord(_ablaut(ablaut), ablaut);
    }

    /**
     * {@inheritDoc}
     * If this word was inflected with a noun case through the {@link #match(BaseFynotekWord)} method, {@link Ablaut#DEFAULT} is returned.
     * @since 2.0
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
    public OldFynotekWord verbTense(Tense tenseOfVerb) {
        return new OldFynotekWord(_ablaut((tenseOfVerb == Tense.GNOMIC ? Tense.HYP_GNOMIC : tenseOfVerb).getAblaut()), tenseOfVerb);
    }

    /**
     * {@inheritDoc}
     * If {@code inflection} is an instance of {@link FynotekWord.Case}, the result is returned with the supplied noun case as its inflection, but with {@link Ablaut#DEFAULT} ablaut applied.
     * @since 3.0
     */
    @Override
    public OldFynotekWord inflect(@Nullable Inflection inflection) {
        return (OldFynotekWord) super.inflect(inflection);
    }

    /**
     * {@inheritDoc}
     * If the word to be matched with is a {@link FynotekWord} marked for a noun case, this word is returned with the FynotekWord's inflection, but with {@link Ablaut#DEFAULT} ablaut applied.
     */
    @Override
    public OldFynotekWord match(BaseFynotekWord word) {
        if (word.inflection == null || word.inflection instanceof FynotekWord.Case) return new OldFynotekWord(beginning, vowels, end, word.inflection);
        if (word.inflection instanceof Tense tenseOfVerb) return verbTense(tenseOfVerb);
        return ablaut((Ablaut) word.inflection);
    }

    @Override
    public OldFynotekWord personSuffix(Person person) {
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
    public static boolean isValidSequence(String sequence) {
        return BaseFynotekWord.isValidSequence(sequence, "[aeiouyptkmnñrfshjw'\\s]", (byte) 2, true);
    }
}
