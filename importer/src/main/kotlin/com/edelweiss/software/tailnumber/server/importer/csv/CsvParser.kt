package com.edelweiss.software.tailnumber.server.importer.csv

import com.google.common.collect.Maps
import org.slf4j.LoggerFactory
import java.io.File

/**
 * A simple CSV (or TSV) parser that returns a [table] of rows.
 *
 * @param isExternalFile true if file is outside of JAR, false otherwise
 */
class CsvParser(val filename: String, val columnDelimiter : Char = ',', val quoteChar: Char? = '"', val isExternalFile: Boolean) {

    private val logger = LoggerFactory.getLogger(CsvParser::class.java)

    private lateinit var _table : List<List<String>>
    private lateinit var _columns : Map<String, Int>

    fun table(skipHeader: Boolean = false): List<List<String>> {
        if (this::_table.isInitialized) return if (skipHeader) _table.drop(1) else _table

        val table: MutableList<List<String>> = mutableListOf()
        var lineNo = 0

        val bufferedReader = if (isExternalFile)
            File(filename).bufferedReader(Charsets.UTF_8)
        else
            inputStream()?.bufferedReader(Charsets.UTF_8)
        check (bufferedReader != null) { "File not found: $filename" }
        bufferedReader.use { reader ->
            reader.forEachLine { line ->
                lineNo++
                if (line.isBlank()) return@forEachLine // ignore empty lines
                try {
                    val row = CsvTokenizer(line, columnDelimiter, quoteChar).toList()
                    table += row
                } catch (t: Throwable) {
                    logger.error("Error tokenizing line $lineNo: $line")
                    throw t
                }
            }
        }

        _table = table
        return if (skipHeader) _table.drop(1) else _table
    }

    fun columns(): Map<String, Int> {
        if (this::_columns.isInitialized) return _columns
        val sanitizedNames = sanitize(columnNames())
        _columns = Maps.toMap(sanitizedNames) { name -> sanitizedNames.indexOf(name) }
        return _columns
    }

    private fun columnNames() : List<String> = table()[0]

    fun getString(column: String, row: List<String>): String? =
        if (columns()[column] != null)
            sanitize(row[columns()[column]!!])
        else
            throw NullPointerException("Column not found: $column")

    fun requireString(column: String, row: List<String>) = getString(column, row)
        ?: throw IllegalArgumentException("No value for column \"$column\"")

    fun getBoolean(column: String, row: List<String>) = getString(column, row).toBoolean()

    fun getInt(column: String, row: List<String>) = try {
        getString(column, row)?.toInt()
    } catch (nfe: NumberFormatException) {
        throw IllegalArgumentException("Column $column", nfe)
    }
    
    fun requireInt(column: String, row: List<String>) = getInt(column, row)
        ?: throw IllegalArgumentException("No value for column \"$column\"")

    fun getChar(column: String, row: List<String>) = getString(column, row)?.let { it[0] }

    fun requireChar(column: String, row: List<String>) = getChar(column, row)
        ?: throw IllegalArgumentException("No value for column \"$column\"")

    fun getDouble(column: String, row: List<String>) = getString(column, row)?.toDouble()
    
    fun requireDouble(column: String, row: List<String>) = getDouble(column, row)
        ?: throw IllegalArgumentException("No value for column \"$column\"")

    private fun sanitize(row: List<String>): List<String?> = row.mapNotNull { s -> sanitize(s) }

    private fun sanitize(s: String?): String? = s
            ?.trim()
            ?.removeNonAscii()
            ?.ifBlank { null }

    private fun String.removeNonAscii() : String {
        val sb = StringBuilder()
        this.toCharArray().forEach { c ->
            if (c in 1.toChar()..127.toChar()) // also removes NUL character
                sb.append(c)
        }
        return sb.toString()
    }

    private fun inputStream() = Thread.currentThread().contextClassLoader.getResourceAsStream(filename)
}