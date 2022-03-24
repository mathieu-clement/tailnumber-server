package com.edelweiss.software.tailnumber.server.importer.faa

import com.edelweiss.software.tailnumber.server.importer.csv.CsvParser

class AcftRefImporter(val filename: String) {
    val parser = CsvParser(filename, isExternalFile = true, quoteChar = null)

    fun import(): List<AcftRefRecord> =
        parser.table().drop(1) // skip header
            .map { row ->
                AcftRefRecord(
                    parser.requireString("CODE", row),
                    parser.requireString("MFR", row),
                    parser.requireString("MODEL", row),
                    parser.getChar("TYPE-ACFT", row),
                    parser.requireInt("TYPE-ENG", row),
                    parser.requireInt("AC-CAT", row),
                    parser.requireInt("BUILD-CERT-IND", row),
                    parser.requireInt("NO-ENG", row),
                    parser.requireInt("NO-SEATS", row),
                    parser.requireString("AC-WEIGHT", row).last().digitToInt(),
                    parser.getInt("SPEED", row)
                )
            }
}

fun main(args: Array<String>) {
    val filename = args[0]
    val importer = AcftRefImporter(filename)
    importer.import()
}