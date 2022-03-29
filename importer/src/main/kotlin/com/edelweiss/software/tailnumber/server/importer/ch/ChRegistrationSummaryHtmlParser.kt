package com.edelweiss.software.tailnumber.server.importer.ch

import com.edelweiss.software.tailnumber.server.core.Country
import com.edelweiss.software.tailnumber.server.core.aircraft.AircraftReference
import com.edelweiss.software.tailnumber.server.core.aircraft.AircraftType
import com.edelweiss.software.tailnumber.server.core.aircraft.Weight
import com.edelweiss.software.tailnumber.server.core.aircraft.WeightUnit
import com.edelweiss.software.tailnumber.server.core.engine.EngineReference
import com.edelweiss.software.tailnumber.server.core.registration.Registration
import com.edelweiss.software.tailnumber.server.core.registration.RegistrationId
import com.edelweiss.software.tailnumber.server.core.registration.UnstructuredRegistrant
import org.apache.commons.text.StringEscapeUtils
import java.io.File

class ChRegistrationSummaryHtmlParser(val filename: String, private val isExternalFile: Boolean) {

    private val aircraftTypeMappings: Map<String, AircraftType> = mapOf(
        "Powered Glider" to AircraftType.POWERED_GLIDER,
        "Glider" to AircraftType.GLIDER,
        "Homebuild Glider" to AircraftType.GLIDER,
        "Homebuilt Glider" to AircraftType.GLIDER, // in case FOCA fixes their typo
        "Balloon (Gas)" to AircraftType.BALLOON,
        "Balloon (Hot-air)" to AircraftType.BALLOON,
        "Helicopter" to AircraftType.ROTORCRAFT,
        "Homebuilt Helicopter" to AircraftType.ROTORCRAFT,
        "Aeroplane" to AircraftType.FIXED_WING_SINGLE_ENGINE, // Modified to Multi engine when applicable
        "Homebuilt Airplane" to AircraftType.FIXED_WING_SINGLE_ENGINE,
        "Airship (Hot-air)" to AircraftType.BLIMP_DIRIGIBLE,
        "Homebuilt Gyrocopter" to AircraftType.GYROPLANE,
        "Ultralight Gyrocopter" to AircraftType.ULTRALIGHT_GYROPLANE,
        "Ecolight" to AircraftType.ECOLIGHT,
        "Trike" to AircraftType.TRIKE,
        "Ultralight (3-axis control)" to AircraftType.ULTRALIGHT_3_AXIS_CONTROL
    )

    private val attrLineRegex = Regex("<b>.+</b>&#160;.*")

    fun import(): List<Registration> {

        val results: MutableList<Registration> = mutableListOf()
        var aircraftType: AircraftType = AircraftType.OTHER
        var homebuilt = false
        var summary = Summary("", AircraftType.OTHER, false)
        var insideRecord = false

        reader().use { reader ->
            while (reader.ready()) {
                val originalLine = reader.readLine()
                val line = removeBr(originalLine)

                if (insideRecord) {
                    if (attrLineRegex.matches(line)) {
                        Tokenizer(
                            StringEscapeUtils.unescapeHtml4(
                                originalLine
                                    .replace("<br/>", " ", true)
                                    .replace("&#160;", "")
                            )
                        ).asMap()
                            .forEach { (label, value) ->
                                when (label) {
                                    "Manufacturer" -> summary.aircraftManufacturer = value
                                    "Aircraft Model/Type" -> summary.aircraftModel = value
                                    "ICAO Aircraft Type" -> summary.icaoType = value
                                    "Year of Manufacture" -> summary.year = value.toInt()
                                    "Serial Number" -> summary.serialNumber = value
                                    "Maximum Take off Mass (MTOM) (kg)" -> summary.maximumTakeOffMass =
                                        value.toDouble().toInt()
                                    "Seating Capacity (MOPSC)" -> summary.seatingCapacity =
                                        if (value != "N/A") value.toInt() else null
                                    "Engine / Propeller" -> parseEngines(value, summary.engineSummaries)
                                    "Main Owner" -> summary.mainOwner = value
                                    "Main Holder" -> summary.mainOperator = value
                                }
                            }
                    } else {
                        insideRecord = false

                        // If engines > 1 and type is FIXED_WING_SINGLE_ENGINE, change to FIXED_WING_MULTI_ENGINE
                        if (aircraftType == AircraftType.FIXED_WING_SINGLE_ENGINE || aircraftType == AircraftType.FIXED_WING_MULTI_ENGINE) {
                            aircraftType =
                                (if (summary.engineSummaries.isNotEmpty() && (summary.engineSummaries.size > 1 || summary.engineSummaries[0].count > 1))
                                    AircraftType.FIXED_WING_MULTI_ENGINE
                                else
                                    AircraftType.FIXED_WING_SINGLE_ENGINE)
                        }

                        results += summary.toRegistration()
                    }
                }

                if (" / " in line && "href" !in line) {
                    aircraftTypeMappings.keys.firstOrNull { "/ $it" in line }?.let { mappingKey ->
                        aircraftType = aircraftTypeMappings[mappingKey]!!
                        homebuilt = "Eigenbau" in line
                    }
                }

                if ("HB-" in line) {
                    insideRecord = true
                    summary = Summary(unwrapBold(line), aircraftType, homebuilt)
                }

                // Record stops when another begins (<b>HB-ABC</b>)
                // or the line "Stand / Etat / Stato / Issue: 27.03.2022<br/>" appears
            }
        }

        return results
    }

    // To internal object for testing
    internal companion object {
        // it might be possible to use a single regex for both...
        private val enginesRegexMulti = Regex("([0-9])x (.+?)(?=[0-9]x)")
        private val enginesRegexSingle = Regex("([0-9])x (.+)")

        internal fun parseEngines(input: String, engineSummaries: MutableList<EngineSummary>) {
            var startIndex = 0
            enginesRegexMulti.findAll(input).forEach { matchResult ->
                addResult(matchResult, engineSummaries)
                startIndex = matchResult.range.last
            }

            if (startIndex < input.length) {
                enginesRegexSingle.find(input.substring(startIndex))?.let { matchResult ->
                    addResult(matchResult, engineSummaries)
                }
            }
        }

        private fun addResult(matchResult: MatchResult, engineSummaries: MutableList<EngineSummary>) {
            val summary = EngineSummary()
            val (match, count, makeAndModel) = matchResult.groupValues
            summary.count = count.toInt()

            val makeAndModelDelimiterIndex = makeAndModel.lastIndexOf(",")
            check(makeAndModelDelimiterIndex != -1) { "Could not find delimiter between engine manufacturer and model: \"$makeAndModel\"" }
            summary.manufacturer = makeAndModel.substring(0, makeAndModelDelimiterIndex)
            summary.model = makeAndModel.substring(makeAndModelDelimiterIndex + ", ".length)

            engineSummaries += summary
        }
    }

    private fun removeBr(line: String) = line
        .replace("<br/>", "", true)

    private fun unwrapBold(line: String) = line
        .replace("<b>", "")
        .replace("</b>", "")

    private fun inputStream() =
        Thread.currentThread().contextClassLoader.getResourceAsStream("pubs.html")

    private fun reader() = if (isExternalFile)
        File(filename).bufferedReader(Charsets.UTF_8)
    else
        inputStream()?.bufferedReader(Charsets.UTF_8)!!

    private class Summary(val tailnumber: String, val aircraftType: AircraftType, val homebuilt: Boolean) {
        var aircraftManufacturer: String? = null
        var aircraftModel: String? = null
        var icaoType: String? = null
        var year: Int? = null
        var serialNumber: String? = null
        var maximumTakeOffMass: Int? = null
        var seatingCapacity: Int? = null
        var engineSummaries: MutableList<EngineSummary> = mutableListOf()
        var mainOwner: String? = null
        var mainOperator: String? = null

        fun toRegistration() = Registration(
            registrationId = RegistrationId(tailnumber, Country.CH),
            aircraftReference = AircraftReference(
                serialNumber = serialNumber,
                aircraftType = aircraftType,
                typeCertificated = !homebuilt,
                manufacturer = aircraftManufacturer,
                model = aircraftModel,
                icaoType = icaoType,
                manufactureYear = year,
                maxTakeOffMass = maximumTakeOffMass?.let { Weight(it, WeightUnit.KILOGRAMS) },
                passengerSeats = seatingCapacity,
                engines = engineSummaries.map { it.count }.takeIf { it.isNotEmpty() }?.reduce { a, b -> a + b }
            ),
            engineReferences = engineSummaries.map {
                EngineReference(
                    count = it.count,
                    manufacturer = it.manufacturer,
                    model = it.model
                )
            },
            owner = mainOwner?.let { UnstructuredRegistrant(it) },
            operator = mainOperator?.let { UnstructuredRegistrant(it) },
        )
    }

    internal class EngineSummary() {
        var count: Int = 1
        var manufacturer: String = ""
        var model: String = ""
    }
}