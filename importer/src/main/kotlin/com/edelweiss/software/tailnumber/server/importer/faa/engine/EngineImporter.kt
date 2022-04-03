package com.edelweiss.software.tailnumber.server.importer.faa.engine

import com.edelweiss.software.tailnumber.server.importer.csv.CsvParser

class EngineImporter(filename: String) {
    private val parser = CsvParser(filename, isExternalFile = true, quoteChar = null)

    fun import(): List<EngineRecord> =
        parser.table().drop(1) // skip header
            .map { row ->
                EngineRecord(
                    parser.requireString("CODE", row),
                    parser.requireString("MFR", row),
                    parser.requireString("MODEL", row),
                    parser.getInt("TYPE", row),
                    parser.getInt("HORSEPOWER", row).takeIf { it != 0 },
                    parser.getInt("THRUST", row).takeIf { it != 0 }
                )
            }
            .filter { it.mfr.lowercase() != "none" && it.model.lowercase() != "none" }
}

fun main(args: Array<String>) {
    val filename = args[0]
    val importer = EngineImporter(filename)
    importer.import().forEach { println(it) }
}