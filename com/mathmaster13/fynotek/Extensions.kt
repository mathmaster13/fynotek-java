package com.mathmaster13.fynotek

operator fun FynotekParent.component1() = this.beginning
operator fun FynotekParent.component2() = this.vowels
operator fun FynotekParent.component3() = this.end

/**
 * Returns the character at the index of the string representation of this word.
 * This function is not recommended for looping over the characters of a `FynotekParent`,
 * as it converts the word to a `String` each time it is called.
 * If looping is needed, convert the word to a `String` with [FynotekParent.toString()] and loop over the `String`.
 */
operator fun FynotekParent.get(index: Int) = this.toString()[index]