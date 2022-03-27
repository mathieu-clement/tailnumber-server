package com.edelweiss.software.tailnumber.server.importer.zipcodes

import com.edelweiss.software.tailnumber.server.importer.csv.CsvParser
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.slf4j.LoggerFactory

class ZipCodeRepository : KoinComponent {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val parser = CsvParser("zip-codes/geo-data.csv", quoteChar = null, isExternalFile = false)

    private val zipcode2State : Map<String, String> = readFile()

    private fun readFile(): Map<String, String> =
        parser.table(skipHeader = true).associate { row ->
            parser.requireString("zipcode", row) to parser.requireString("state_abbr", row)
        }

    fun state(zipCode: String) : String? = zipcode2State[zipCode]

//    operator fun get(zipCode: String) : String? = state(zipCode)
}

fun main() {
    startKoin {
        modules(module {
            single { ZipCodeRepository() }
        })

        val repo = koin.get<ZipCodeRepository>()
        listOf("94131", "94040", "95070", "95033").forEach { zipCode ->
            println("$zipCode: ${repo.state(zipCode)}")
        }
    }
}