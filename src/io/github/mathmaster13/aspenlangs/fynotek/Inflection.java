package io.github.mathmaster13.aspenlangs.fynotek;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents a Fynotek inflection type.
 * This can be an <i>ambiguous</i> inflection type, such as I ablaut, which can represent past tense or genitive case,
 * or a <i>specific</i> inflection type, which is a specific case or tense, such as the genitive case.
 * Specific inflection types have their own subinterface, {@link SpecificInflection}.
 *
 * A {@code null} inflection represents a word's root form—the form of a word with no inflection.
 * Note that this is not the same as a word being marked for the present tense, nominative case, or default ablaut—those forms <i>are</i> inflections!
 *
 * Prior to version 3.0, {@code Inflection} represented what is now known as {@link SpecificInflection},
 * @since 3.0
 */
public sealed interface Inflection permits SpecificInflection, BaseFynotekWord.Ablaut {
    /**
     * An array containing all constants that extend this interface.
     * Contains all values from {@link SpecificInflection#values} {@link BaseFynotekWord.Ablaut#values()}.
     * @since 3.0
     */
    @NotNull Inflection[] values = getValues();
    private static Inflection[] getValues() {
        ArrayList<Inflection> temp = new ArrayList<>(Arrays.asList((Inflection[]) SpecificInflection.values));
        temp.addAll(Arrays.asList((Inflection[]) BaseFynotekWord.Ablaut.values()));
        return temp.toArray(new Inflection[0]);
    }

    /**
     * Returns the ablaut associated with this Inflection.
     * If you need to get the ablaut of a specific word, use {@link BaseFynotekWord#getAblaut()} and <i>not</i> this function.
     * @return the ablaut associated with this Inflection.
     * @see BaseFynotekWord#getAblaut()
     */
    @NotNull BaseFynotekWord.Ablaut getAblaut();
}
