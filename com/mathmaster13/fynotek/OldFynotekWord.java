package com.mathmaster13.fynotek;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 A class for handling words in an older version of Fynotek. All objects created by this class are immutable. Old Fynotek documentation can be found <a href="https://docs.google.com/document/d/1U66rWinK0Qy-xab_ifZ4KC4c95icbjSgiUk9Qc27-80/edit">here</a>.
 @author mathmaster13
 @since 1.0
 */
public final class OldFynotekWord extends FynotekParent {
    // Public constructors
    /**
     Converts a String into an OldFynotekWord. Leading and trailing whitespace is ignored (the <code>String.trim()</code> method is called on <code>word</code>).
     @param word word to be converted to an OldFynotekWord.
     */
    public OldFynotekWord(@NotNull String word) {
        super(word, Ablaut.NONE);
    }


    // Private constructors
    private OldFynotekWord(@NotNull String a, @NotNull String b, @NotNull String c, @NotNull Ablaut ablaut) {
        super(a, b, c, ablaut);
    }
    private OldFynotekWord(@NotNull String word, @NotNull Ablaut ablaut) {
        super(word, ablaut);
    }
    private OldFynotekWord(@NotNull FynotekParent word) {
        super(word);
    }


    // Internal-use methods
    @Override
    protected @NotNull OldFynotekWord ablaut(@NotNull Ablaut vowel) {
        if (vowel == Ablaut.NONE) return this;
        if (vowels.isEmpty()) return new OldFynotekWord(beginning, vowels, end, ablaut);
        final String newVowels;
        if (vowel == Ablaut.REDUPLICATION) {
            int vowelLength = vowels.length();
            String vowelToReduplicate = (vowelLength == 1 ? vowels : Character.toString(vowels.charAt(vowelLength - 1)));
            newVowels = vowelToReduplicate + "'" + vowelToReduplicate;
        } else {
            String ablautAsString = Character.toString(vowel.asChar);
            newVowels = (vowels.equals(ablautAsString) ? Character.toString(vowel.ablautPair) : ablautAsString);
        }
        return new OldFynotekWord(beginning, newVowels, end, vowel);
    }


    // Public methods
    /**
     Returns this OldFynotekWord inflected for the verb tense specified by <code>tenseOfVerb</code>. Note that <code>verbTense(Tense.GNOMIC)</code> and <code>verbTense(Tense.HYP_GNOMIC)</code> will always return the same result.
     @param tenseOfVerb the verb tense to inflect this OldFynotekWord for.
     @return this OldFynotekWord inflected for the specified verb tense.
     @see #match(FynotekParent)
     @see FynotekParent#verbTense(Tense)
     */
    @Override
    public @NotNull OldFynotekWord verbTense(@NotNull Tense tenseOfVerb) {
        return new OldFynotekWord(super.verbTense((tenseOfVerb == Tense.GNOMIC) ? Tense.HYP_GNOMIC : tenseOfVerb));
    }

    @Override
    public @NotNull OldFynotekWord personSuffix(@NotNull Person person) {
        if (person == Person.P1) return this;
        String suffix = (person == Person.P2 ? "a" : "o");
        if (end.isEmpty()) suffix = "n" + suffix;
        return new OldFynotekWord(this.toString() + suffix, ablaut);
    }

    /**
     Returns whether the given sequence is phonotactically and orthographically valid in old Fynotek. Capitalization is ignored (for example, <code>"A"</code> and <code>"a"</code> are treated the same way). Multiple words can be separated by whitespace, and this function will only return <code>true</code> if all words in <code>sequence</code> are valid.  Leading and trailing whitespace is ignored (the <code>String.trim()</code> method is called on <code>sequence</code>). A sequence containing punctuation marks, numbers, or other non-letter characters (with the exception of <code>'</code>) returns <code>false</code>, as well as an empty sequence or one containing only whitespace.
     @param sequence the sequence to be checked for validity.
     @return <code>true</code> if <code>sequence</code> is a valid sequence, and <code>false</code> if otherwise.
     */
    public static boolean isValidSequence(@NotNull String sequence) {
        return FynotekParent.isValidSequence(sequence, "[aeiouyptkmnñrfshjw'\\s]", (byte) 2, true);
    }
}
