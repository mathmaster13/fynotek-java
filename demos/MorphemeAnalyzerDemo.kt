@file:JvmName("MorphemeAnalyzerDemo")

// TODO thinky thonk. should you make some of these functions part of the main API?
// TODO numbers and ñojera (derogatory)
// TODO to verb or not to verb

import com.mathmaster13.fynotek.BaseFynotekWord.Ablaut
import com.mathmaster13.fynotek.FynotekWord
import com.mathmaster13.fynotek.FynotekWord.separateVowels
import com.mathmaster13.fynotek.FynotekWord.isValidSequence

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
/** A list of all content words, excluding numbers. */
val contentWords = hashSetOf("pynsi", "ay", "fmutue", "samlaa", "kisak", "mees", "ofu", "pyn", "siofu", "jera", "ñojera") // TODO fill this (derogatory)

val possessorSuffixes = hashSetOf(
    "akiy",
    "ami",
    "asi",
    "ejiy",
    "ejimi",
    "imi",
    "oli",
    "ui",
    "uin",
    "umii",
    "umiy",
    "unin",
    "yrami",
    "yri"
)
val numberSuffixes = hashSetOf("po", "pura", "poña", "sola", "manta", "tauwa")

fun main() {
    println("This tool will parse a Fynotek word into individual morphemes. You can then look each morpheme up in a morpheme dictionary, such as https://mathmaster13.github.io/fynotek/dict/.")

    // Prompt
    print("Enter a Fynotek word (NOT a proper noun) to be analyzed:  ")
    val word = java.util.Scanner(System.`in`).next().lowercase()

    // an
    if (word.matches(Regex("^an+$"))) {
        println("an")
        return
    }

    // Basic checks
    if (!isValidSequence(word)) {
        println("This is not a valid Fynotek word. Please try again.")
        return
    }
    if (standaloneWords.contains(word)) {
        println(word)
        return
    }

    // TODO untested code alert
    // Analyze
    val analysisList = analyze(word)
    analysisList.forEach { println(it) }
    if (analysisList.isEmpty()) println("No valid analyses can be found. This may be because you have entered a proper noun (which is not supported), or because you have entered morphemes that cannot coexist in the same word.\nIf this is truly a valid Fynotek word, there may be an error in the code or the dictionary it uses, and you should report an issue on GitHub at https://github.com/mathmaster13/fynotek-java/.")
}

/**
 * Assuming that a word is a single word with no affixes (but it can have ablaut), give the possible dictionary matches.
 */
private fun singleRootAblautAnalysis(word: String): List<Analysis> {
    if (contentWords.contains(word)) return listOf(Analysis(arrayOf(word)))
    // ñojera (if there is more than one jera)
    if (word.matches(Regex("^ñojera(jera)+$")))
        return listOf(Analysis("(ñojera" + " + jera".repeat((word.length - 6) / 4) + ")"))

    val separatedWord = separateVowels(word)
    val pattern = Regex("^" + separatedWord[0] + "[aeiouy]{1,2}" + separatedWord[2] + "$")
    val output = mutableListOf<Analysis>()
    for (dictWord in contentWords) {
        if (!dictWord.matches(pattern)) continue
        // We now know that this is an ablaut form (and not a root form)
        output.addAll(checkForAblaut(dictWord, separatedWord))
    }
    // ñojera (if there is more than one jera)
    if (word.matches(Regex("^ñojera(jera)*?jer[aeiouy]{1,2}$"))) {
        for (analysis in checkForAblaut("ñojera" + "jera".repeat((word.length - 6) / 4), separatedWord))
            output.add(Analysis(arrayOf("(ñojera" + " + jera".repeat((analysis.text[0].length - 6) / 4) + ")", analysis.text[1])))
    }
    return output
}

// dictWord is from the dictionary; separatedWord is the word you're testing
private fun checkForAblaut(dictWord: String, separatedWord: Array<String>): List<Analysis> {
    val output = mutableListOf<Analysis>()
    val word = separatedWord[0] + separatedWord[1] + separatedWord[2]

    // Check for ablaut
    val rootWord = FynotekWord(dictWord)
    val reduplicated = (separatedWord[1].length == 2) && (separatedWord[1][0] == separatedWord[1][1])
    for (i in separatedWord[1].indices) {
        if (reduplicated && i >= 1) break
        val ablautToCheck = Ablaut.valueOf(separatedWord[1][i].uppercase())
        if (rootWord.ablaut(ablautToCheck).toString() == word) output.add(Analysis(arrayOf(dictWord, "$ablautToCheck ablaut")))
    }
    if (separatedWord[1].length == 1 || reduplicated) {
        // Check for reduplication ablaut
        if (rootWord.ablaut(Ablaut.REDUPLICATION).toString() == word) output.add(Analysis(arrayOf(dictWord, "reduplication ablaut")))
    }
    return output
}

/** A single-root analysis that accounts for suffixes, but not prefixes. */
private fun singleRootAnalysis(word: String, isVerb: Boolean?): List<Analysis> {
    val output = singleRootAblautAnalysis(word).toMutableList() // Check if any analysis works with no suffix
    // deal with numbers here

    for (suffix in getValidSuffixes(isVerb)) {
        if (!word.contains(Regex("$suffix$"))) continue
        var potentialRoot = word.substring(0, word.length - suffix.length)
        // Check if any analysis works assuming no filler letters
        singleRootAblautAnalysis(potentialRoot).forEach { output.add((it + Analysis(arrayOf(suffixToString(suffix)), suffixToBoolean(suffix)))!!) }

        // Check if filler letters are valid
        // Note: Fynotek shouldn't have any one-letter content roots, but if it does, add (potentialRoot.length <= 1) to the below condition.
        if (!potentialRoot.contains(Regex("[an]$"))) continue
        potentialRoot = potentialRoot.substring(0, potentialRoot.length - 1)
        if (isValidSequence(potentialRoot + suffix)) continue // If filler letters aren't necessary, they won't be used.
        singleRootAblautAnalysis(potentialRoot).forEach { output.add((it + Analysis(arrayOf(suffixToString(suffix)), suffixToBoolean(suffix)))!!) }
    }
    // TODO nanpa (ike)
    return output
}

private fun getValidSuffixes(isVerb: Boolean?): Array<String> {
    if (isVerb == true) return arrayOf("ñy", "ñya", "ñyo")
    if (isVerb == false) return arrayOf("ak", "akñy", "ñy")
    return arrayOf("ak", "akñy", "ñy", "ñya", "ñyo")
}

private fun suffixToBoolean(suffix: String): Boolean? = when {
    suffix.contains(Regex("^ak")) -> false
    suffix.contains(Regex("[ao]$")) -> true
    else -> null
}

private fun suffixToString(suffix: String) = when (suffix) {
    "akñy" -> "ak + ñy"
    "ñya" -> "ñy + a"
    "ñyo" -> "ñy + o"
    else -> suffix
}

private fun attachedModifierAnalysis(word: String, isVerb: Boolean?): List<Analysis> {
    // TODO check if ambiguity makes returning a list necessary, or if i can just return one analysis
    // TODO this code is untested
    // TODO reminder to handle numbers (sigh)
    val output = mutableListOf<Analysis>()
    // Check for single content word analyses
    if (contentWords.contains(word)) output.add(Analysis(word))
    // ñojera (if there is more than one jera)
    if (word.matches(Regex("^ñojera(jera)+$")))
        output.add(Analysis("(ñojera" + " + jera".repeat((word.length - 6) / 4) + ")"))

    // If the word is a verb, posessor suffixes don't apply!
    if (isVerb == true) return output
    
    // Very similar to singleRootAnalysis. TODO perhaps make these use one function?
    for (suffix in possessorSuffixes) {
        if (!word.contains(Regex("$suffix$"))) continue
        var potentialRoot = word.substring(0, word.length - suffix.length)
        // Check if any analysis works assuming no filler letters
        if (contentWords.contains(potentialRoot)) output.add(Analysis(arrayOf(potentialRoot, possessorSuffixToAnalysis(suffix)), false))
        // ñojera (if there is more than one jera)
        if (potentialRoot.matches(Regex("^ñojera(jera)+$")))
            output.add(Analysis(arrayOf("(ñojera" + " + jera".repeat((word.length - 6) / 4) + ")", possessorSuffixToAnalysis(suffix)), false))

        // Check if filler letters are valid
        // Note: Fynotek shouldn't have any one-letter content roots, but if it does, add (potentialRoot.length <= 1) to the below condition.
        if (!potentialRoot.contains(Regex("[an]$"))) continue
        potentialRoot = potentialRoot.substring(0, potentialRoot.length - 1)
        if (isValidSequence(potentialRoot + suffix)) continue // If filler letters aren't necessary, they won't be used.
        if (contentWords.contains(potentialRoot)) output.add(Analysis(arrayOf(potentialRoot, possessorSuffixToAnalysis(suffix)), false))
        // ñojera (if there is more than one jera)
        if (potentialRoot.matches(Regex("^ñojera(jera)+$")))
            output.add(Analysis(arrayOf("(ñojera" + " + jera".repeat((word.length - 6) / 4) + ")", possessorSuffixToAnalysis(suffix)), false))
    }
    return output
}

fun possessorSuffixToAnalysis(suffix: String): String {
    return "possessive form of " + when (suffix) {
        "akiy" -> "ñaki"
        "ami" -> "yumiame"
        "asi" -> "saraso"
        "ejiy" -> "eji"
        "ejimi" -> "ejime"
        "imi" -> "ñakime"
        "oli" -> "folo"
        "ui" -> "tua"
        "uin" -> "juon"
        "umii" -> "yumia"
        "umiy" -> "yumi"
        "unin" -> "junon"
        "yrami" -> "yrame"
        "yri" -> "yra"
        else -> throw AssertionError("A posessor suffix was probably left out of the list. Please report an issue on GitHub with the word that you entered and this error message.")
    }
}

/**
 * Everything but the prefix.
 * If isVerb is null, we don't know the part of speech of the word.
 */
private fun fullAnalysisNoPrefix(word: String, isVerb: Boolean?): List<Analysis> {
    // TODO untested
    val output = singleRootAnalysis(word, isVerb).toMutableList() // Try to analyze the word as one word
    for (i in 1 until word.length) {
        // Very similar to singleRootAnalysis. TODO perhaps make these use one function?
        var potentialRoot = word.substring(0, i)
        val potentialModifier = word.substring(i, word.length)
        // Check if any analysis works assuming no filler letters
        for (rootAnalysis in singleRootAnalysis(potentialRoot, isVerb))
            for (modifierAnalysis in attachedModifierAnalysis(potentialModifier, isVerb)) {
                val combinedAnalysis = rootAnalysis + modifierAnalysis
                if (combinedAnalysis != null) output.add(combinedAnalysis)
            }

        // Check if filler letters are valid
        // Note: Fynotek shouldn't have any one-letter content roots, but if it does, add (potentialRoot.length <= 1) to the below condition.
        if (!potentialRoot.contains(Regex("[an]$"))) continue
        potentialRoot = potentialRoot.substring(0, potentialRoot.length - 1)
        if (isValidSequence(potentialRoot + potentialModifier)) continue // If filler letters aren't necessary, they won't be used.
        for (rootAnalysis in singleRootAnalysis(potentialRoot, isVerb))
            for (modifierAnalysis in attachedModifierAnalysis(potentialModifier, isVerb)) {
                val combinedAnalysis = rootAnalysis + modifierAnalysis
                if (combinedAnalysis != null) output.add(combinedAnalysis)
            }
    }
    return output
}

fun analyze(word: String): List<Analysis> {
    // TODO perhaps move everything from main() into here so that this can be run standalone?
    val output = fullAnalysisNoPrefix(word, null).toMutableList() // check for analyses without any prefix
    if (!word.contains(Regex("^[aoi]"))) return output

    val prefix = word[0].toString()
    val isVerb = (word[0] == 'i')
    // Similar to singleRootAnalysis, but probably not enough to make it use one function.
    var potentialWord = word.substring(1, word.length)
    // Check if any analysis works assuming no filler letters
    fullAnalysisNoPrefix(potentialWord, isVerb).forEach { output.add((Analysis(prefix, isVerb) + it)!!) }
    // If an NPE happens up there, we have a problem.

    // Check if filler letters are present
    // Note: Fynotek shouldn't have any one-letter content roots, but if it does, add (potentialWord.length <= 1) to the below condition.
    if (!potentialWord.contains(Regex("^[an]"))) return output
    potentialWord = potentialWord.substring(1, potentialWord.length)
    if (isValidSequence(word[0] + potentialWord)) return output // If filler letters aren't necessary, they won't be used.
    fullAnalysisNoPrefix(potentialWord, isVerb).forEach { output.add((Analysis(prefix, isVerb) + it)!!) }
    // If an NPE happens up there, we have a problem.
    return output
}

// TODO ordinals are modifier-only, so we need something more than a boolean
// isVerb refers to the *root* word's part of speech, not the modifier's.
class Analysis(@JvmField val text: Array<String>, @JvmField val isVerb: Boolean? = null) {
    constructor(text: String, isVerb: Boolean? = null) : this(arrayOf(text), isVerb)
    operator fun plus(other: Analysis): Analysis? {
        if (this.isVerb != null && other.isVerb != null && this.isVerb != other.isVerb) return null
        return Analysis(text + other.text, isVerb)
    }
    override fun toString(): String {
        if (text.isEmpty()) return ""
        var output = text[0]
        for (i in 1 until text.size) {
            output += " + " + text[i]
        }
        return output
    }
}