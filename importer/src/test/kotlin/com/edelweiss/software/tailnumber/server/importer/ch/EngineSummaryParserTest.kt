package com.edelweiss.software.tailnumber.server.importer.ch

import com.edelweiss.software.tailnumber.server.importer.ch.ChRegistrationSummaryImporter.Companion.parseEngines
import com.edelweiss.software.tailnumber.server.importer.ch.ChRegistrationSummaryImporter.EngineSummary
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EngineSummaryParserTest {

    private val engineSummaries = mutableListOf<EngineSummary>()

    @Test
    fun simple1Engine() {
        val input = "1x LIMBACH FLUGMOTOREN GMBH & CO. KG, L 2000 EB 1"
        parseEngines(input, engineSummaries)
        assertEquals(1, engineSummaries.size)
        val summary = engineSummaries[0]
        assertEquals(1, summary.count)
        assertEquals("LIMBACH FLUGMOTOREN GMBH & CO. KG", summary.manufacturer)
        assertEquals("L 2000 EB 1", summary.model)
    }

    @Test
    fun twoSameEngines() {
        val input = "2x ROLLS-ROYCE PLC, TAY 611-8"
        parseEngines(input, engineSummaries)
        assertEquals(1, engineSummaries.size)
        val summary = engineSummaries[0]
        assertEquals(2, summary.count)
        assertEquals("ROLLS-ROYCE PLC", summary.manufacturer)
        assertEquals("TAY 611-8", summary.model)
    }

    @Test
    fun twoDifferentEngines() {
        val input = "1x SNECMA, CFM56-5B4/31x SNECMA, CFM56-5B4/2P"
        parseEngines(input, engineSummaries)

        val summary1 = engineSummaries[0]
        assertEquals(1, summary1.count)
        assertEquals("SNECMA", summary1.manufacturer)
        assertEquals("CFM56-5B4/3", summary1.model)

        assertEquals(2, engineSummaries.size)

        val summary2 = engineSummaries[1]
        assertEquals(1, summary2.count)
        assertEquals("SNECMA", summary2.manufacturer)
        assertEquals("CFM56-5B4/2P", summary2.model)
    }

    @Test
    fun threeDifferentEngines() {
        val input = "1x SNECMA, CFM56-5B4/32x SNECMA, CFM56-5B4/2P3x ABC, DEF, GHI"
        parseEngines(input, engineSummaries)

        val summary1 = engineSummaries[0]
        assertEquals(1, summary1.count)
        assertEquals("SNECMA", summary1.manufacturer)
        assertEquals("CFM56-5B4/3", summary1.model)

        assertEquals(3, engineSummaries.size)

        val summary2 = engineSummaries[1]
        assertEquals(2, summary2.count)
        assertEquals("SNECMA", summary2.manufacturer)
        assertEquals("CFM56-5B4/2P", summary2.model)

        val summary3 = engineSummaries[2]
        assertEquals(3, summary3.count)
        assertEquals("ABC, DEF", summary3.manufacturer)
        assertEquals("GHI", summary3.model)
    }

    @Test
    fun commaInManufacturerName() {
        val input = "1x BOMBARDIER-ROTAX GMBH GUNSKIRCHEN, A, ROTAX 914 UL2"
        parseEngines(input, engineSummaries)
        assertEquals(1, engineSummaries.size)
        val summary = engineSummaries[0]
        assertEquals(1, summary.count)
        assertEquals("BOMBARDIER-ROTAX GMBH GUNSKIRCHEN, A", summary.manufacturer)
        assertEquals("ROTAX 914 UL2", summary.model)
    }
}