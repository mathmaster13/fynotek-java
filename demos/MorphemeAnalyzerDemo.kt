@file:JvmName("MorphemeAnalyzerDemo")

// TODO thinky thonk. should you make some of these functions part of the main API?

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

val numberRoots = arrayOf("fui", "ay", "fo", "us", "nos", "pur")
val numberSuffixes = arrayOf("po", "pura", "poña", "sola", "manta", "tauwa")

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
private fun singleRootAblautAnalysis(word: String): MutableList<Analysis> {
    if (contentWords.contains(word)) return mutableListOf(Analysis(word))
    // ñojera (if there is more than one jera)
    if (word.matches(Regex("^ñojera(jera)+$")))
        return mutableListOf(Analysis("(ñojera" + " + jera".repeat((word.length - 6) / 4) + ")"))

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
private fun checkForAblaut(dictWord: String, separatedWord: Array<String>): MutableList<Analysis> {
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

private fun numericAnalysis(word: String, isAttachedModifier: Boolean): MutableList<Analysis> {
    // TODO date/time words
    val output = numericAblautAnalysis(word, !isAttachedModifier)
    // TODO this is similar to singleRootAnalysis. maybe make this its own function?
    // make an array of all the date and time suffixes if needed
    for (suffix in (if (isAttachedModifier) arrayOf("yla", "ylarea", "rea") else arrayOf("rea"))) {
        if (!word.contains(Regex("$suffix$"))) continue
        var potentialRoot = word.substring(0, word.length - suffix.length)
        // Check if any analysis works assuming no filler letters
        numericAblautAnalysis(potentialRoot, !isAttachedModifier).forEach { output.add((it + Analysis(if (suffix == "ylarea") arrayOf("yla", "rea") else arrayOf(suffix), /* TODO fill in this part of speech */))!!) }

        // Check if filler letters are valid
        // Note: Fynotek shouldn't have any one-letter content roots, but if it does, add (potentialRoot.length <= 1) to the below condition.
        if (!potentialRoot.contains(Regex("[an]$"))) continue
        potentialRoot = potentialRoot.substring(0, potentialRoot.length - 1)
        if (isValidSequence(potentialRoot + suffix)) continue // If filler letters aren't necessary, they won't be used.
        numericAblautAnalysis(potentialRoot, !isAttachedModifier).forEach { output.add((it + Analysis(if (suffix == "ylarea") arrayOf("yla", "rea") else arrayOf(suffix), /* TODO fill in this part of speech */))!!) }
    }
    return output
}

private fun numericAblautAnalysis(word: String, checkForAblaut: Boolean): MutableList<Analysis> {
    if (word.length < 2) return mutableListOf() // if it's less than 2, it's not a number!
    if (numberRoots.contains(word)) return mutableListOf(Analysis(word))

    val output = mutableListOf<Analysis>()
    val separatedWord = separateVowels(word)

    // ablaut only
    if (checkForAblaut)
        for (number in numberRoots) output.addAll(checkForAblaut(number, separatedWord))

    if (output.isNotEmpty() && output[0][0] == "fui") return output

    // ablaut + suffix
    // find the root number! cannot be "fui".
    // TODO this assumes that only one root number is possible. verify this!
    val rootAnalyses = mutableListOf<Analysis>()
    var rootLength = 0
    findRoot@for (i in 1 until numberRoots.size) {
        val potentialNumber = numberRoots[i]
        // no ablaut
        if (word.length >= potentialNumber.length && word.substring(0, potentialNumber.length) == potentialNumber) {
            rootLength = potentialNumber.length
            break
        }

        // yes ablaut
        // Test for a valid root. This will either be the same length or 1 more than the length of the root, so check both!
        if (checkForAblaut)
            for (j in potentialNumber.length..kotlin.math.min(word.length, potentialNumber.length + 1))
                if (rootAnalyses.addAll(checkForAblaut(potentialNumber, separateVowels(word.substring(0, j))))) {
                    rootLength = j
                    break@findRoot
                }
    }
    if (rootLength == 0) return mutableListOf() // if nothing shows up here, it's not a number! this is also the same as if (rootAnalyses.isNotEmpty()).

    println("DEBUG: rootAnalyses: $rootAnalyses") // FIXME debug; remove this later

    // Thankfully, number suffixes shouldn't need filler letters!
    var lengthToCut = rootLength
    val suffixes = mutableListOf<String>()
    for (potentialSuffix in numberSuffixes) {
        val cutSuffix = word.substring(lengthToCut)
        if (!cutSuffix.contains(Regex(if (cutSuffix == "po") "^po(?!ña)" else "^suffix"))) continue
        lengthToCut += potentialSuffix.length
        suffixes.add(potentialSuffix)
    }
    if (lengthToCut == word.length) // If there are additional letters after the suffix, we have a problem.
        rootAnalyses.forEach { output.add((it + Analysis(suffixes.toTypedArray()))!!) } // NPE = very bad
    println(output)
    return output
}

/** A single-root analysis that accounts for suffixes, but not prefixes. */
private fun singleRootAnalysis(word: String, possiblePartsOfSpeech: HashSet<PartOfSpeech> = PartOfSpeech.ALL): MutableList<Analysis> {
    val output = singleRootAblautAnalysis(word) // Check if any analysis works with no suffix
    // nanpa (ike a)
    output.addAll(numericAnalysis(word, false))

    for (suffix in getValidSuffixes(possiblePartsOfSpeech)) {
        if (!word.contains(Regex("$suffix$"))) continue
        var potentialRoot = word.substring(0, word.length - suffix.length)
        // Check if any analysis works assuming no filler letters
        singleRootAblautAnalysis(potentialRoot).forEach { output.add((it + Analysis(suffixToString(suffix), suffixToPartsOfSpeech(suffix)))!!) }
        numericAnalysis(potentialRoot, false).forEach { output.add((it + Analysis(suffixToString(suffix), suffixToPartsOfSpeech(suffix)))!!) }

        // Check if filler letters are valid
        // Note: Fynotek shouldn't have any one-letter content roots, but if it does, add (potentialRoot.length <= 1) to the below condition.
        if (!potentialRoot.contains(Regex("[an]$"))) continue
        potentialRoot = potentialRoot.substring(0, potentialRoot.length - 1)
        if (isValidSequence(potentialRoot + suffix)) continue // If filler letters aren't necessary, they won't be used.
        singleRootAblautAnalysis(potentialRoot).forEach { output.add((it + Analysis(suffixToString(suffix), suffixToPartsOfSpeech(suffix)))!!) }
        numericAnalysis(potentialRoot, false).forEach { output.add((it + Analysis(suffixToString(suffix), suffixToPartsOfSpeech(suffix)))!!) }
    }
    // ak and rea cannot go together! TODO see if this is true; it may not be anymore!
//    output.forEach { if (it.text.contains("ak") && it.text.contains("rea")) output.remove(it) }
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

private fun attachedModifierAnalysis(word: String, possiblePartsOfSpeech: HashSet<PartOfSpeech> = PartOfSpeech.ALL): MutableList<Analysis> {
    // TODO check if ambiguity makes returning a list necessary, or if i can just return one analysis
    // TODO reminder to handle numbers (sigh)
    val output = mutableListOf<Analysis>()
    // Check for single content word analyses
    if (contentWords.contains(word)) output.add(Analysis(word))
    // ñojera (if there is more than one jera)
    if (word.matches(Regex("^ñojera(jera)+$")))
        output.add(Analysis("(ñojera" + " + jera".repeat((word.length - 6) / 4) + ")"))
    // nanpa (ike a)
    output.addAll(numericAnalysis(word, true))

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
        // nanpa (ike a)
        numericAnalysis(potentialRoot, true).forEach {
            // TODO see if the null check is necessary
            val combinedAnalysis = it + Analysis(possessorSuffixToAnalysis(suffix), PartOfSpeech.NOUN_OR_VERB)
            if (combinedAnalysis != null) output.add(combinedAnalysis)
        }

        // Check if filler letters are valid
        // Note: Fynotek shouldn't have any one-letter content roots, but if it does, add (potentialRoot.length <= 1) to the below condition.
        if (!potentialRoot.contains(Regex("[an]$"))) continue
        potentialRoot = potentialRoot.substring(0, potentialRoot.length - 1)
        if (isValidSequence(potentialRoot + suffix)) continue // If filler letters aren't necessary, they won't be used.
        if (contentWords.contains(potentialRoot)) output.add(Analysis(arrayOf(potentialRoot, possessorSuffixToAnalysis(suffix)), PartOfSpeech.NOUN_OR_VERB))
        // ñojera (if there is more than one jera)
        if (potentialRoot.matches(Regex("^ñojera(jera)+$")))
            output.add(Analysis(arrayOf("(ñojera" + " + jera".repeat((word.length - 6) / 4) + ")", possessorSuffixToAnalysis(suffix)), PartOfSpeech.NOUN_OR_VERB))
        // nanpa (ike a)
        numericAnalysis(potentialRoot, true).forEach {
            // TODO see if the null check is necessary
            val combinedAnalysis = it + Analysis(possessorSuffixToAnalysis(suffix), PartOfSpeech.NOUN_OR_VERB)
            if (combinedAnalysis != null) output.add(combinedAnalysis)
        }
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
private fun fullAnalysisNoPrefix(word: String, possiblePartsOfSpeech: HashSet<PartOfSpeech> = PartOfSpeech.ALL): MutableList<Analysis> {
    val output = singleRootAnalysis(word, possiblePartsOfSpeech) // Try to analyze the word as one word
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
    val output = fullAnalysisNoPrefix(word) // check for analyses without any prefix
    if (!word.contains(Regex("^[aoi]"))) return output

    println("prefix being evaluated")

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
    operator fun get(index: Int) = text[index]
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