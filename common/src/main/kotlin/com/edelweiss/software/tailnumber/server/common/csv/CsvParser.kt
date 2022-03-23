package com.edelweiss.software.tailnumber.server.common.csv

import com.google.common.collect.Maps
import org.slf4j.LoggerFactory

/**
 * A simple CSV (or TSV) parser that returns a [table] of rows.
 *
 * Logs a warning if it encounters an empty or null value unless [shouldWarnNullOrEmpty] is `False`.
 */
abstract class CsvParser {
    companion object {
        val logger = LoggerFactory.getLogger(CsvParser::class.java)
        val QUOTE_CHAR = '"'
    }

    private lateinit var _table : List<List<String>>
    private lateinit var _columns : Map<String, Int>

    protected open fun columnDelimiter(): Char =
        when {
            fileName().endsWith(".tsv") -> '\t'
            fileName().endsWith(".csv") -> ','
            else -> TODO("File type is not supported (case-sensitive) or subclass must support it")
        }

    protected abstract fun fileName(): String

    protected abstract fun estimatedNumRows(): Int

    open fun table(): List<List<String>> {
        if (this::_table.isInitialized) return _table

        val columnDelimiter = columnDelimiter()

        val table: MutableList<List<String>> = ArrayList(estimatedNumRows())
        var lineNo = 0

        val bufferedReader = inputStream()?.bufferedReader(Charsets.UTF_8)
        check (bufferedReader != null) { "File not found: ${fileName()}" }
        bufferedReader.use { reader ->
            reader.forEachLine { line ->
                lineNo++
                if (line.isBlank() && skipEmptyRows()) return@forEachLine // ignore empty lines
                try {
                    val row = CsvTokenizer(line, columnDelimiter, QUOTE_CHAR).toList()
                    table += row
                } catch (t: Throwable) {
                    logger.error("Error tokenizing line $lineNo: $line")
                    throw t
                }
            }
        }

        _table = table
        return table
    }

    fun columns(): Map<String, Int> {
        if (this::_columns.isInitialized) return _columns
        val sanitizedNames = sanitize(columnNames())
        _columns = Maps.toMap(sanitizedNames) { name -> sanitizedNames.indexOf(name) }
        return _columns
    }

    protected open fun columnNames() : List<String> = table()[0]

    open fun getString(column: String, row: List<String>): String? =
        if (columns()[column] != null)
            sanitize(row[columns()[column]!!])
        else
            throw NullPointerException("Column not found: $column")

    fun requireString(column: String, row: List<String>) = getString(column, row)
        ?: throw IllegalArgumentException("No value for column \"$column\"")

    fun getBoolean(column: String, row: List<String>) = getString(column, row).toBoolean()

    fun getInt(column: String, row: List<String>) = getString(column, row)?.toInt()
    
    fun requireInt(column: String, row: List<String>) = getInt(column, row)
        ?: throw IllegalArgumentException("No value for column \"$column\"")

    fun getDouble(column: String, row: List<String>) = getString(column, row)?.toDouble()
    
    fun requireDouble(column: String, row: List<String>) = getDouble(column, row)
        ?: throw IllegalArgumentException("No value for column \"$column\"")

    private fun sanitize(row: List<String>): List<String?> = row.map { s -> sanitize(s) }

    protected fun sanitize(s: String?): String? = s?.trim()?.ifBlank { null }

    private fun inputStream() = Thread.currentThread().contextClassLoader.getResourceAsStream(fileName())

    protected open fun skipEmptyRows() = true

}