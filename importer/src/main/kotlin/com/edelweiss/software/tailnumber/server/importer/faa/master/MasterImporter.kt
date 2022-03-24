package com.edelweiss.software.tailnumber.server.importer.faa.master

import com.edelweiss.software.tailnumber.server.importer.csv.CsvParser

class MasterImporter(filename: String) {
    private val parser = CsvParser(filename, isExternalFile = true, quoteChar = null)

    fun import(): List<MasterRecord> =
        parser.table().drop(1) // skip header
            .map { row ->
                MasterRecord(
                    parser.requireString("N-NUMBER", row),
                    parser.requireString("SERIAL NUMBER", row),
                    parser.requireString("MFR MDL CODE", row),
                    parser.getString("ENG MFR MDL", row),
                    parser.getInt("YEAR MFR", row),
                    parser.getInt("TYPE REGISTRANT", row),
                    parser.getString("NAME", row),
                    parser.getString("STREET", row),
                    parser.getString("STREET2", row),
                    parser.getString("CITY", row),
                    parser.getString("STATE", row),
                    parser.getString("ZIP CODE", row),
                    parser.getChar("REGION", row),
                    parser.getInt("COUNTY", row),
                    parser.getString("COUNTRY", row),
                    parser.requireString("LAST ACTION DATE", row),
                    parser.getString("CERT ISSUE DATE", row),
                    parser.getString("CERTIFICATION", row),
                    parser.requireChar("TYPE AIRCRAFT", row),
                    parser.requireInt("TYPE ENGINE", row),
                    parser.requireString("STATUS CODE", row),
                    parser.requireString("MODE S CODE", row),
                    parser.getString("FRACT OWNER", row),
                    parser.getString("AIR WORTH DATE", row),
                    parser.getString("OTHER NAMES(1)", row),
                    parser.getString("OTHER NAMES(2)", row),
                    parser.getString("OTHER NAMES(3)", row),
                    parser.getString("OTHER NAMES(4)", row),
                    parser.getString("OTHER NAMES(5)", row),
                    parser.getString("EXPIRATION DATE", row),
                    parser.requireString("UNIQUE ID", row),
                    parser.getString("KIT MFR", row),
                    parser.getString("KIT MODEL", row),
                    parser.requireString("MODE S CODE HEX", row)
                )
            }
}

fun main(args: Array<String>) {
    val filename = args[0]
    val importer = MasterImporter(filename)
    importer.import().forEach { println(it) }
}