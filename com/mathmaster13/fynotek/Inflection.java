package com.mathmaster13.fynotek;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a Fynotek inflection type—namely, a noun case or verb tense.
 * @since 2.0
 */
public sealed interface Inflection permits FynotekWord.Case, FynotekParent.Tense {
    /**
     * Returns the ablaut type associated with this Inflection.
     * If you need to get the ablaut of a specific word, use {@link FynotekParent#getAblaut()} and <i>not</i> this function.
     * @return the ablaut type associated with this Inflection.
     */
    public @NotNull FynotekParent.Ablaut getAblaut();
}
