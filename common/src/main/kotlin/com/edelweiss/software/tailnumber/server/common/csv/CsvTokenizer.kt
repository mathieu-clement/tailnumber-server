package com.edelweiss.software.tailnumber.server.common.csv

class CsvTokenizer(private val line: String,
                   private val delimiter: Char,
                   private val quoteChar : Char) {
    private var left = 0
    private var right = 0
    private var insideQuotes = false
    private var endsWithDelimiter = false
    private var endsWithDelimiterConsumed = false
    private var token : String = ""

    fun hasNext() = right < line.length || (endsWithDelimiter && !endsWithDelimiterConsumed)

    fun next() : String {
        if (endsWithDelimiter && !endsWithDelimiterConsumed) {
            endsWithDelimiterConsumed = true
        }
        token = nextToken()
        return token
    }

    fun toList() : List<String> {
        val lst = mutableListOf<String>()
        while (hasNext()) lst += next()
        return lst
    }

    private fun nextToken() : String {
        if (endsWithDelimiter) return ""

        check(hasNext())
        left = right

        while (right < line.length) {
            val c = line[right]
            if (c == delimiter) {
                if (insideQuotes) {
                    right++
                } else {
                    val token = line.substring(left, right)
                    right++
                    if (right == line.length) {
                        endsWithDelimiter = true
                    }
                    return token
                }
            } else if (c == quoteChar) {
                if (insideQuotes) {
                    insideQuotes = false
                    val token = line.substring(left, right) // i.e. not including the quote
                    right++ // skip over quote character
                    if (right < line.length) {
                        assert(line[right] == delimiter) // should be delimiter following
                        right++ // skip over delimiter
                    }
                    return token
                } else {
                    insideQuotes = true
                    left++
                    right++
                }
            } else {
                right++
            }
        }

        return line.substring(left, right)
    }
}