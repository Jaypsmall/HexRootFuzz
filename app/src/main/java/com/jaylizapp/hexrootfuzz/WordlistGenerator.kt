package com.jaylizapp.hexrootfuzz

import java.io.File
import java.util.Locale

class WordlistGenerator {

    fun transformWord(
        word: String,
        case: Boolean,
        leet: Boolean,
        prefixes: List<String>,
        suffixes: List<String>,
        numbers: Boolean
    ): List<String> {
        val baseVariants = mutableSetOf(word)
        if (case) {
            baseVariants.add(word.lowercase(Locale.ROOT))
            baseVariants.add(word.uppercase(Locale.ROOT))
            baseVariants.add(word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() })
        }

        if (leet) {
            val leetMap = mapOf(
                'a' to '4', 'e' to '3', 'i' to '1', 'o' to '0', 's' to '5', 't' to '7',
                'A' to '4', 'E' to '3', 'I' to '1', 'O' to '0', 'S' to '5', 'T' to '7'
            )
            val currentVariants = baseVariants.toList()
            for (v in currentVariants) {
                val leetWord = v.map { leetMap[it] ?: it }.joinToString("")
                baseVariants.add(leetWord)
            }
        }

        val results = mutableListOf<String>()
        val prefixList = if (prefixes.isEmpty()) listOf("") else prefixes + ""
        val suffixList = if (suffixes.isEmpty()) listOf("") else suffixes + ""
        val numberRange = if (numbers) (0..99).map { it.toString().padStart(2, '0') } else listOf("")

        for (v in baseVariants) {
            for (p in prefixList) {
                for (s in suffixList) {
                    for (n in numberRange) {
                        val result = "$p$v$s$n"
                        if (result.isNotEmpty()) {
                            results.add(result)
                        }
                    }
                }
            }
        }
        return results.distinct()
    }
}
