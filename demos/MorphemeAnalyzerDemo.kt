@file:JvmName("MorphemeAnalyzerDemo")

// TODO thinky thonk. should you make some of these functions part of the main API?
// TODO numbers and ñojera (derogatory)
// TODO to verb or not to verb

import com.mathmaster13.fynotek.BaseFynotekWord.Ablaut
import com.mathmaster13.fynotek.FynotekWord
import com.mathmaster13.fynotek.FynotekWord.separateVowels
import com.mathmaster13.fynotek.FynotekWord.isValidSequence

val input: java.util.Scanner = java.util.Scanner(System.`in`)

/** A list of all Fynotek words that cannot be inflected. */
val standaloneWords = hashSetOf(
    "an",
    "ñet",
    "ñeta",
    "oht",
    "ohto",
    "sta",
    "stayla",
    "staula",
    "stañy",
    "stañyyla",
    "stañyula",
    "ak",
    "i",
    "ik",
    "ka",
    "ki",
    "ni",
    "niyla",
    "niula",
    "to",
    "in",
    "nep",
    "nu",
    "ñes",
    "ñue",
    "oni",
    "ot",
    "poriu",
    "rek",
    "sy",
    "yf"
)
val ablautValidRoots = hashSetOf("pynsi", "ay", "fmutue", "samlaa", "kisak", "mees") // TODO fill this (derogatory)

fun main() {
    val word = prompt("Enter a fynotek word (NOT a proper noun) to be analyzed:  ")
    if (!isValidSequence(word)) {
        println("This is not a valid fynotek word. Please try again.")
        return
    }
    if (standaloneWords.contains(word)) {
        println(word)
        return
    }
    for (analysis in singleRootAnalysis(word)) println(analysis)
}

// Convenience functions
private fun prompt(message: String): String {
    print(message)
    return input.next()
}

/** Assuming that a word is a single word with no affixes (but it can have ablaut), give the possible dictionary matches */
private fun singleRootAblautAnalysis(word: String): List<String> {
    if (ablautValidRoots.contains(word)) return listOf(word)
    val separatedWord = separateVowels(word)
    val pattern = Regex("^" + separatedWord[0] + "[aeiouy]{1,2}" + separatedWord[2] + "$")
    val output = mutableListOf<String>()
    for (dictWord in ablautValidRoots) {
        if (!dictWord.matches(pattern)) continue
        // We now know that this is an ablaut form (and not a root form)
        // Check for ablaut
        val rootWord = FynotekWord(dictWord)
        val reduplicated = (separatedWord[1].length == 2) && (separatedWord[1][0] == separatedWord[1][1])
        for (i in separatedWord[1].indices) {
            if (reduplicated && i >= 1) break
            val ablautToCheck = Ablaut.valueOf(separatedWord[1][i].uppercase())
            if (rootWord.ablaut(ablautToCheck).toString() == word) output.add("$dictWord + $ablautToCheck ablaut")
        }
        if (separatedWord[1].length == 1 || reduplicated) {
            // Check for reduplication ablaut
            if (rootWord.ablaut(Ablaut.REDUPLICATION).toString() == word) output.add("$dictWord + reduplication ablaut")
        }
    }
    return output
}

/** A single-root analysis that accounts for suffixes, but not prefixes. */
private fun singleRootAnalysis(word: String): List<String> {
    val output = mutableListOf<String>()
    output.addAll(singleRootAblautAnalysis(word)) // Check if any analysis works with no suffix
    for (suffix in arrayOf("ak", "akñy", "ñy", "ñya", "ñyo")) {
        if (!word.contains(Regex("$suffix$"))) continue
        var potentialRoot = word.substring(0, word.length - suffix.length)
         // Check if any analysis works assuming no filler letters
        singleRootAblautAnalysis(potentialRoot).forEach { output.add("$it + ${suffixToString(suffix)}") }

        // Check if filler letters are valid
        // Note: Fynotek shouldn't have any one-letter content roots, but if it does, add (potentialRoot.length <= 1) to the below condition.
        if (!potentialRoot.contains(Regex("[an]$"))) continue
        potentialRoot = potentialRoot.substring(0, potentialRoot.length - 1)
        if (isValidSequence(potentialRoot + suffix)) continue // If filler letters aren't necessary, they won't be used.
        singleRootAblautAnalysis(potentialRoot).forEach { output.add("$it + ${suffixToString(suffix)}") }
    }
    return output
}

private fun suffixToString(suffix: String) = when (suffix) {
    "akñy" -> "ak + ñy"
    "ñya" -> "ñy + a"
    "ñyo" -> "ñy + o"
    else -> suffix
}