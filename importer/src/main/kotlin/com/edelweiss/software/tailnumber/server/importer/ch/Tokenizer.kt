package com.edelweiss.software.tailnumber.server.importer.ch

internal class Tokenizer(private val input: String) {

    init {
        require("&#160;" !in input)
        require("<br/>" !in input)
    }

    private var i = 0

    fun hasNext() : Boolean = i < input.length

    fun next() : Token {
        check(hasNext())
        val remaining = input.substring(i)
        return when {
            remaining.length >= 3 && remaining.startsWith("<b>") -> {
                val value = remaining
                    .substringAfter("<b>")
                    .substringBefore("</b>", "MISSING")
                check(value != "MISSING") { "Could not find ending b tag" }
                i += "<b>$value</b>".length
                LabelToken(value.trim())
            }
            else -> {
                val value = remaining.substringBefore("<b")
                i += value.length
                ValueToken(value.trim())
            }
        }
    }

    open class Token(open val value: String)
    data class LabelToken(override var value: String) : Token(value)
    data class ValueToken(override var value: String) : Token(value)

    /**
     * Consumes the tokenizer and returns as a map of <String, String> (label, value)
     */
    fun asMap() : Map<String, String> {
        val map: MutableMap<String, String> = mutableMapOf()
        var label = ""
        while (hasNext()) {
            when (val token = next()) {
                is LabelToken -> label = token.value
                is ValueToken -> {
                    map[label] = token.value
                }
            }
        }
        return map
    }
}