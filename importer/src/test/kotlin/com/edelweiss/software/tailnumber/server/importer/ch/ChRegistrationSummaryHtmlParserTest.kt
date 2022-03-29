package com.edelweiss.software.tailnumber.server.importer.ch

import com.edelweiss.software.tailnumber.server.core.Country
import com.edelweiss.software.tailnumber.server.core.aircraft.AircraftType
import com.edelweiss.software.tailnumber.server.core.aircraft.Weight
import com.edelweiss.software.tailnumber.server.core.aircraft.WeightUnit
import com.edelweiss.software.tailnumber.server.core.registration.Registration
import com.edelweiss.software.tailnumber.server.core.registration.UnstructuredRegistrant
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChRegistrationSummaryHtmlParserTest {

    private val parser = ChRegistrationSummaryHtmlParser()
    private val registrations: Map<String, Registration> = parser.import().associateBy { it.registrationId.id }

    @Test
    fun kow() {
        assertReg(
            "HB-KOW",
            "CONSTRUCTIONS AERONAUTIQUES DE BOURGOGNE",
            "ROBIN DR 400/180",
            "DR40",
            2008,
            "2651",
            1100,
            3,
            1,
            "LYCOMING ENGINES",
            "O-360-A1P",
            "Air-Club d'Yverdon-les-Bains, Ecole de vol à moteur, chemin de l'Aérodrome 2, 1400 Yverdon-les-Bains, Switzerland",
        )
    }

    @Test
    fun seating0() {
        assertEquals(0, registrations["HB-2053"]?.aircraftReference?.seats)
    }

    @Test
    fun icaoTypeSeparateLine() {
        assertEquals("VENT", registrations["HB-2173"]?.aircraftReference?.icaoType)
    }

    @Test
    fun balloonWithNoEngine() {
        val reg = registrations["HB-QYV"]
        assertNull(reg?.engineReferences)
        assertNull(reg?.aircraftReference?.engines)
        assertEquals(AircraftType.BALLOON, reg?.aircraftReference?.aircraftType)
    }

    @Test
    fun engineManufacturerWithComma() {
        val reg = registrations["HB-XDY"]
        assertEquals("DETROIT DIESEL ALLISON, GENERAL MOTORS CORPORATION", reg?.engineReferences?.get(0)?.manufacturer)
        assertEquals("250-C20", reg?.engineReferences?.get(0)?.model)
    }

    @Test
    fun twoEngines() {
        val reg = registrations["HB-ZHG"]
        assertEquals(2, reg?.aircraftReference?.engines)
        assertEquals(1, reg?.engineReferences?.size)
        assertEquals(2, reg?.engineReferences?.get(0)?.count)
    }

    @Test
    fun serialNumberWithSlash() {
        val reg = registrations["HB-QPO"]
        assertEquals("130/75", reg?.aircraftReference?.serialNumber)
    }

    @Test
    fun twoEngineTypes() {
        val reg = registrations["HB-IJM"]
        assertEquals(2, reg?.aircraftReference?.engines)
        val engines = reg?.engineReferences!!
        assertEquals(2, engines.size)
        assertTrue(engines.all { it.manufacturer == "SNECMA" })
        assertEquals(1, engines.count { it.model == "CFM56-5B4/3" })
        assertEquals(1, engines.count { it.model == "CFM56-5B4/2P" })
    }

    @Test
    fun airship() {
        val reg = registrations["HB-BVI"]
        assertEquals(AircraftType.BLIMP_DIRIGIBLE, reg?.aircraftReference?.aircraftType)
    }

    @Test
    fun gliderSeating0() {
        val reg = registrations["HB-1478"]
        assertEquals(0, reg?.aircraftReference?.passengerSeats)
        assertEquals(AircraftType.GLIDER, reg?.aircraftReference?.aircraftType)
    }

    @Test
    fun homeBuiltZZZZ() {
        val reg = registrations["HB-SUN"]
        assertEquals(false, reg?.aircraftReference?.typeCertificated)
        assertEquals("ZZZZ", reg?.aircraftReference?.icaoType)
    }

    @Test
    fun poweredGlider() {
        val reg = registrations["HB-2146"]
        assertEquals(AircraftType.POWERED_GLIDER, reg?.aircraftReference?.aircraftType)
    }

    @Test
    fun homebuiltGyro() {
        val reg = registrations["HB-YSS"]
        assertEquals(AircraftType.GYROPLANE, reg?.aircraftReference?.aircraftType)
        assertEquals(false, reg?.aircraftReference?.typeCertificated)
    }

    @Test
    fun ultraLightGyro() {
        val reg = registrations["HB-WGA"]
        assertEquals(AircraftType.ULTRALIGHT_GYROPLANE, reg?.aircraftReference?.aircraftType)
    }

    @Test
    fun homebuiltHelicopter() {
        val reg = registrations["HB-YFA"]
        assertEquals(AircraftType.ROTORCRAFT, reg?.aircraftReference?.aircraftType)
        assertEquals(false, reg?.aircraftReference?.typeCertificated)
    }

    @Test
    fun homebuiltGlider() {
        val reg = registrations["HB-1227"]
        assertEquals(AircraftType.GLIDER, reg?.aircraftReference?.aircraftType)
        assertEquals(false, reg?.aircraftReference?.typeCertificated)
    }

    @Test
    fun trike() {
        val reg = registrations["HB-STA"]
        assertEquals(AircraftType.TRIKE, reg?.aircraftReference?.aircraftType)
    }

    @Test
    fun ultraLight3Axis() {
        val reg = registrations["HB-5555"]
        assertEquals(AircraftType.ULTRALIGHT_3_AXIS_CONTROL, reg?.aircraftReference?.aircraftType)
    }

    @Test
    fun bermuda() {
        val reg = registrations["HB-JRJ"]!!
        assertTrue("Bermuda" in reg.owner?.value!!)
        assertTrue("Jet Aviation Zurich-Airport AG" in reg.operator?.value!!)
    }

    @Test
    fun longAddress() {
        val reg = registrations["HB-IJW"]!!
        assertEquals(
            "International Lease Finance, Corporation (ILFC), " +
                    "c/o Froriep Renggli / Frau Dunja Koch, " +
                    "Bellerivestrasse 201, 8032 Zürich, Switzerland",
            reg.owner?.value
        )
        assertEquals("Edelweiss Air AG, 8058 Zürich, P.O. Box Switzerland", reg.operator?.value)
    }

    // Address related:
    // KOZ      owner address with missing comma
    // ZFH      owner address with missing comma
    // 2053     owner address
    // 2097     "c/o" address
    // CDE      weird address with P.O. Box
    // CGN      P.O. Box
    // EQD      address on two lines
    // FVA      "Case postale"
    // IJW      long address and short address
    // IOL      address in Ireland
    // JRQ      Panama

    private fun assertReg(
        tailnumber: String,
        manufacturer: String,
        model: String,
        icaoType: String,
        year: Int,
        serialNumber: String,
        maxTakeOffMassKg: Int,
        seatingCapacity: Int,
        engines: Int,
        engineManufacturer: String,
        engineModel: String,
        mainOwner: String,
        mainOperator: String = mainOwner,
    ) {
        val r = registrations[tailnumber]!!
        assertEquals(tailnumber, r.registrationId.id)
        assertEquals(Country.CH, r.registrationId.country)
        assertEquals(manufacturer, r.aircraftReference.manufacturer)
        assertEquals(model, r.aircraftReference.model)
        assertEquals(icaoType, r.aircraftReference.icaoType)
        assertEquals(year, r.aircraftReference.manufactureYear)
        assertEquals(serialNumber, r.aircraftReference.serialNumber)
        assertEquals(Weight(maxTakeOffMassKg, WeightUnit.KILOGRAMS), r.aircraftReference.maxTakeOffMass)
        assertEquals(seatingCapacity, r.aircraftReference.seats)
        assertEquals(engines, r.aircraftReference.engines)
        assertEquals(engineManufacturer, r.engineReferences?.get(0)?.manufacturer)
        assertEquals(engineModel, r.engineReferences?.get(0)?.model)

        assertEquals(UnstructuredRegistrant(mainOwner), r.registrant)
        assertEquals(UnstructuredRegistrant(mainOperator), r.registrant)
    }
}