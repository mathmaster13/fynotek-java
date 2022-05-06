package com.mathmaster13.fynotek;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents a specific Fynotek inflection type—namely, a noun case or verb tense.
 *
 * Prior to version __, this concept was represented by {@link Inflection}.
 * TODO since
 */
public sealed interface SpecificInflection extends Inflection permits BaseFynotekWord.Tense, FynotekWord.Case {
    /**
     * An array containing all constants that extend this interface.
     * Contains all values from {@link BaseFynotekWord.Tense#values()}, and {@link FynotekWord.Case#values()}.
     * TODO since
     */
    @NotNull SpecificInflection[] values = getValues();
    private static SpecificInflection[] getValues() {
        ArrayList<SpecificInflection> temp = new ArrayList<>(Arrays.asList((SpecificInflection[]) BaseFynotekWord.Tense.values()));
        temp.addAll(Arrays.asList((SpecificInflection[]) FynotekWord.Case.values()));
        return temp.toArray(new SpecificInflection[0]);
    }
}
