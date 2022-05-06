@file:JvmName("MorphemeAnalyzerDemo")

// TODO thinky thonk. should you make some of these functions part of the main API?
// TODO numbers (derogatory)

import com.mathmaster13.fynotek.BaseFynotekWord.Ablaut
import com.mathmaster13.fynotek.FynotekWord
import com.mathmaster13.fynotek.FynotekWord.separateVowels
import com.mathmaster13.fynotek.FynotekWord.isValidSequence
import Analysis.PartOfSpeech

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

    // Analyze
    val analysisList = analyze(word)
    analysisList.forEach { println(it) }
    if (analysisList.isEmpty()) println("""No valid analyses can be found. This may be because you have entered a proper noun (which is not supported), or because you have entered morphemes that cannot coexist in the same word.
        |This usually occurs when you try to apply a possessor suffix to a verb, apply "ak" to a verb or ordinal number, or apply ablaut other than A, I, or O ablaut to a noun.
        |If this is truly a valid Fynotek word, there may be an error in the code or the dictionary it uses, and you should report an issue on GitHub at https://github.com/mathmaster13/fynotek-java/.""".trimMargin())
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
        if (rootWord.ablaut(ablautToCheck).toString() == word) {
            val isVerbOrModifierOnly = when (ablautToCheck) {
                Ablaut.O, Ablaut.I, Ablaut.A -> (ablautToCheck == Ablaut.O && dictWord == "folo") // It's only verb-or-modifier-only if it's "folou"
                Ablaut.E,  Ablaut.U, Ablaut.Y -> true
                else -> throw AssertionError("The only way this could have happened is if reduplication or default ablaut got in here, and I don't know how it could have done that. Please report an issue on GitHub with the word that you entered and this error message.")
            }
            output.add(Analysis(arrayOf(dictWord, "$ablautToCheck ablaut"), if (isVerbOrModifierOnly) PartOfSpeech.VERB_OR_MODIFIER else PartOfSpeech.ALL))
        }
    }
    if (separatedWord[1].length == 1 || reduplicated) {
        // Check for reduplication ablaut
        if (rootWord.ablaut(Ablaut.REDUPLICATION).toString() == word) output.add(Analysis(arrayOf(dictWord, "reduplication ablaut"), PartOfSpeech.VERB_OR_MODIFIER))
    }
    return output
}

/** A single-root analysis that accounts for suffixes, but not prefixes. */
private fun singleRootAnalysis(word: String, possiblePartsOfSpeech: HashSet<PartOfSpeech> = PartOfSpeech.ALL): List<Analysis> {
    val output = singleRootAblautAnalysis(word).toMutableList() // Check if any analysis works with no suffix
    // deal with numbers here

    for (suffix in getValidSuffixes(possiblePartsOfSpeech)) {
        if (!word.contains(Regex("$suffix$"))) continue
        var potentialRoot = word.substring(0, word.length - suffix.length)
        // Check if any analysis works assuming no filler letters
        singleRootAblautAnalysis(potentialRoot).forEach { output.add((it + Analysis(suffixToString(suffix), suffixToPartsOfSpeech(suffix)))!!) }

        // Check if filler letters are valid
        // Note: Fynotek shouldn't have any one-letter content roots, but if it does, add (potentialRoot.length <= 1) to the below condition.
        if (!potentialRoot.contains(Regex("[an]$"))) continue
        potentialRoot = potentialRoot.substring(0, potentialRoot.length - 1)
        if (isValidSequence(potentialRoot + suffix)) continue // If filler letters aren't necessary, they won't be used.
        singleRootAblautAnalysis(potentialRoot).forEach { output.add((it + Analysis(suffixToString(suffix), suffixToPartsOfSpeech(suffix)))!!) }
    }
    // TODO nanpa (ike)
    return output
}

private fun getValidSuffixes(possiblePartsOfSpeech: HashSet<PartOfSpeech>): Array<String> {
    val output = mutableListOf("ñy")
    if (possiblePartsOfSpeech.contains(PartOfSpeech.VERB)) output.addAll(arrayOf("ñya", "ñyo"))
    if (possiblePartsOfSpeech.contains(PartOfSpeech.NOUN) || possiblePartsOfSpeech.contains(PartOfSpeech.MODIFIER)) output.addAll(arrayOf("ak", "akñy"))
    return output.toTypedArray()
}

private fun suffixToPartsOfSpeech(suffix: String): HashSet<PartOfSpeech> = when {
    suffix.contains(Regex("^ak")) -> PartOfSpeech.NOUN_OR_VERB
    suffix.contains(Regex("[ao]$")) -> PartOfSpeech.VERB_OR_MODIFIER
    else -> PartOfSpeech.ALL
}

private fun suffixToString(suffix: String) = when (suffix) {
    "akñy" -> arrayOf("ak", "ñy")
    "ñya" -> arrayOf("ñy", "a")
    "ñyo" -> arrayOf("ñy", "o")
    "ak", "ñy" -> arrayOf(suffix)
    else -> throw AssertionError("A basic suffix may have been left out of the list. Please report an issue on GitHub with the word that you entered and this error message.")
}

private fun attachedModifierAnalysis(word: String, possiblePartsOfSpeech: HashSet<PartOfSpeech> = PartOfSpeech.ALL): List<Analysis> {
    // TODO check if ambiguity makes returning a list necessary, or if i can just return one analysis
    // TODO reminder to handle numbers (sigh)
    val output = mutableListOf<Analysis>()
    // Check for single content word analyses
    if (contentWords.contains(word)) output.add(Analysis(word))
    // ñojera (if there is more than one jera)
    if (word.matches(Regex("^ñojera(jera)+$")))
        output.add(Analysis("(ñojera" + " + jera".repeat((word.length - 6) / 4) + ")"))

    // If the word is a verb, posessor suffixes don't apply!
    if (possiblePartsOfSpeech == PartOfSpeech.VERB.asHashSet) return output
    
    // Very similar to singleRootAnalysis. TODO perhaps make these use one function?
    for (suffix in possessorSuffixes) {
        if (!word.contains(Regex("$suffix$"))) continue
        var potentialRoot = word.substring(0, word.length - suffix.length)
        // Check if any analysis works assuming no filler letters
        if (contentWords.contains(potentialRoot)) output.add(Analysis(arrayOf(potentialRoot, possessorSuffixToAnalysis(suffix)), PartOfSpeech.NOUN_OR_VERB))
        // ñojera (if there is more than one jera)
        if (potentialRoot.matches(Regex("^ñojera(jera)+$")))
            output.add(Analysis(arrayOf("(ñojera" + " + jera".repeat((word.length - 6) / 4) + ")", possessorSuffixToAnalysis(suffix)), PartOfSpeech.NOUN_OR_VERB))

        // Check if filler letters are valid
        // Note: Fynotek shouldn't have any one-letter content roots, but if it does, add (potentialRoot.length <= 1) to the below condition.
        if (!potentialRoot.contains(Regex("[an]$"))) continue
        potentialRoot = potentialRoot.substring(0, potentialRoot.length - 1)
        if (isValidSequence(potentialRoot + suffix)) continue // If filler letters aren't necessary, they won't be used.
        if (contentWords.contains(potentialRoot)) output.add(Analysis(arrayOf(potentialRoot, possessorSuffixToAnalysis(suffix)), PartOfSpeech.NOUN_OR_VERB))
        // ñojera (if there is more than one jera)
        if (potentialRoot.matches(Regex("^ñojera(jera)+$")))
            output.add(Analysis(arrayOf("(ñojera" + " + jera".repeat((word.length - 6) / 4) + ")", possessorSuffixToAnalysis(suffix)), PartOfSpeech.NOUN_OR_VERB))
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
private fun fullAnalysisNoPrefix(word: String, possiblePartsOfSpeech: HashSet<PartOfSpeech> = PartOfSpeech.ALL): List<Analysis> {
    val output = singleRootAnalysis(word, possiblePartsOfSpeech).toMutableList() // Try to analyze the word as one word
    for (i in 1 until word.length) {
        // Very similar to singleRootAnalysis. TODO perhaps make these use one function?
        var potentialRoot = word.substring(0, i)
        val potentialModifier = word.substring(i, word.length)
        // Check if any analysis works assuming no filler letters
        for (rootAnalysis in singleRootAnalysis(potentialRoot, possiblePartsOfSpeech))
            for (modifierAnalysis in attachedModifierAnalysis(potentialModifier, possiblePartsOfSpeech)) {
                val combinedAnalysis = rootAnalysis + modifierAnalysis
                if (combinedAnalysis != null) output.add(combinedAnalysis)
            }

        // Check if filler letters are valid
        // Note: Fynotek shouldn't have any one-letter content roots, but if it does, add (potentialRoot.length <= 1) to the below condition.
        if (!potentialRoot.contains(Regex("[an]$"))) continue
        potentialRoot = potentialRoot.substring(0, potentialRoot.length - 1)
        if (isValidSequence(potentialRoot + potentialModifier)) continue // If filler letters aren't necessary, they won't be used.
        for (rootAnalysis in singleRootAnalysis(potentialRoot, possiblePartsOfSpeech))
            for (modifierAnalysis in attachedModifierAnalysis(potentialModifier, possiblePartsOfSpeech)) {
                val combinedAnalysis = rootAnalysis + modifierAnalysis
                if (combinedAnalysis != null) output.add(combinedAnalysis)
            }
    }
    return output
}

fun analyze(word: String): List<Analysis> {
    // TODO perhaps move everything from main() into here so that this can be run standalone?
    val output = fullAnalysisNoPrefix(word).toMutableList() // check for analyses without any prefix
    if (!word.contains(Regex("^[aoi]"))) return output

    val prefix = word[0].toString()
    val partOfSpeech = PartOfSpeech.get(word[0])
    // Similar to singleRootAnalysis, but probably not enough to make it use one function.
    var potentialWord = word.substring(1, word.length)
    // Check if any analysis works assuming no filler letters
    fullAnalysisNoPrefix(potentialWord, partOfSpeech.asHashSet).forEach {
        val combinedAnalysis = Analysis(prefix, partOfSpeech) + it
        if (combinedAnalysis != null) output.add(combinedAnalysis)
    }

    // Check if filler letters are present
    // Note: Fynotek shouldn't have any one-letter content roots, but if it does, add (potentialWord.length <= 1) to the below condition.
    if (!potentialWord.contains(Regex("^[an]"))) return output
    potentialWord = potentialWord.substring(1, potentialWord.length)
    if (isValidSequence(word[0] + potentialWord)) return output // If filler letters aren't necessary, they won't be used.
    fullAnalysisNoPrefix(potentialWord, partOfSpeech.asHashSet).forEach {
        val combinedAnalysis = Analysis(prefix, partOfSpeech) + it
        if (combinedAnalysis != null) output.add(combinedAnalysis)
    }
    return output
}

// TODO ordinals are modifier-only, so we need something more than a boolean
// isVerb refers to the *root* word's part of speech, not the modifier's.
// TODO perhaps allow text to contain strings and/or arrays, for stuff like ñojera or rea
class Analysis(@JvmField val text: Array<String>, @JvmField val possiblePartsOfSpeech: HashSet<PartOfSpeech> = PartOfSpeech.ALL) {
    init {
        if (possiblePartsOfSpeech.isEmpty()) throw IllegalArgumentException("Part of speech array cannot be empty")
    }
    constructor(text: String, possiblePartsOfSpeech: HashSet<PartOfSpeech> = PartOfSpeech.ALL) : this(arrayOf(text), possiblePartsOfSpeech)
//    constructor(text: Array<String>, partOfSpeech: PartOfSpeech) : this(text, partOfSpeech.asHashSet)
    constructor(text: String, partOfSpeech: PartOfSpeech) : this(arrayOf(text), partOfSpeech.asHashSet)

    operator fun plus(other: Analysis): Analysis? {
        // set intersect but better
        val combinedPartsOfSpeech = HashSet(possiblePartsOfSpeech)
        combinedPartsOfSpeech.retainAll(other.possiblePartsOfSpeech)

        if (combinedPartsOfSpeech.isEmpty()) return null
        return Analysis(text + other.text, combinedPartsOfSpeech)
    }
    override fun toString(): String {
        if (text.isEmpty()) return ""
        var output = text[0]
        for (i in 1 until text.size) output += " + " + text[i]
        return output
    }

    // There are other parts of speech, but this class only deals with these three.
    // Other parts of speech will only be added if the usage of this class expands.
    enum class PartOfSpeech {
        NOUN, VERB, MODIFIER;
        @JvmField val asHashSet = hashSetOf(this)
        companion object { // companion objects /neg
            @JvmField val ALL = values().toHashSet()
            @JvmField val NOUN_OR_VERB = hashSetOf(NOUN, VERB)
            @JvmField val VERB_OR_MODIFIER = hashSetOf(VERB, MODIFIER)
            @JvmStatic fun get(prefix: Char) = when (prefix) {
                'a' -> NOUN
                'i' -> VERB
                'o' -> MODIFIER
                else -> throw IllegalArgumentException("prefix must be 'a', 'i', or 'o'")
            }
        }
    }
}