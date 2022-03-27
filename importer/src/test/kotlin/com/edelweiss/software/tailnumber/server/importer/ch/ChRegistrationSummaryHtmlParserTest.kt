package com.edelweiss.software.tailnumber.server.importer.ch

import com.edelweiss.software.tailnumber.server.core.Country
import com.edelweiss.software.tailnumber.server.core.aircraft.AircraftType
import com.edelweiss.software.tailnumber.server.core.aircraft.Weight
import com.edelweiss.software.tailnumber.server.core.aircraft.WeightUnit
import com.edelweiss.software.tailnumber.server.core.registration.Registration
import com.edelweiss.software.tailnumber.server.core.registration.UnstructuredRegistrant
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
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
    fun noEngineBalloon() {
        val reg = registrations["HB-QYV"]
        assertNull(reg?.engineReference)
        assertNull(reg?.aircraftReference?.engines)
        assertEquals(AircraftType.BALLOON, reg?.aircraftReference?.aircraftType)
    }

    // Reg      interesting aspect(s)
    // XDY      Engine / Propeller has two commas
    // ZHG      2 engines
    // QPO      serial number with slash
    // IJM      2 engine types
    // BVI      Airship (dirigible)
    // 1478     Glider, seating 0
    // SUN      Homebuilt, icaoType ZZZZ
    // 2146     Powered glider
    // YSS      Homebuilt gyrocopter
    // WGA      Ultralight gyrocopter
    // YFA      Homebuilt helicopter
    // 1227     Homebuilt glider
    // STA      Trike
    // 5555     Ultra-light (3 axis control)

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
    // JRJ      Bermuda
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
        assertEquals(engineManufacturer, r.engineReference?.manufacturer)
        assertEquals(engineModel, r.engineReference?.model)

        assertEquals(UnstructuredRegistrant(mainOwner), r.registrant)
        assertEquals(UnstructuredRegistrant(mainOperator), r.registrant)
    }
}