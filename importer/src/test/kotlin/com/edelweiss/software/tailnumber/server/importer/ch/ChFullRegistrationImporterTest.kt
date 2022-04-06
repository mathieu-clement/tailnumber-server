package com.edelweiss.software.tailnumber.server.importer.ch

import com.edelweiss.software.tailnumber.server.core.Country
import com.edelweiss.software.tailnumber.server.core.aircraft.AircraftReference
import com.edelweiss.software.tailnumber.server.core.aircraft.AircraftType
import com.edelweiss.software.tailnumber.server.core.aircraft.Weight
import com.edelweiss.software.tailnumber.server.core.aircraft.WeightUnit
import com.edelweiss.software.tailnumber.server.core.engine.EngineReference
import com.edelweiss.software.tailnumber.server.core.registration.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class ChFullRegistrationImporterTest {

    private val importer = ChFullRegistrationImporter("" /* unused in this test */)

    @Test
    fun fetchRegistration() {
        val string = readFileAsString("HB-5002.json")
        val registrationId = RegistrationId("HB-5002", Country.CH)
        val registration = importer.deserializeRegistration(registrationId, string)
        val registrant = Registrant(
            name = "Donat Bösch",
            address = Address(
                street1 ="Taleze 38",
                city = "Balzers",
                zipCode = "9496",
                country = "Switzerland"
            )
        )
        assertEquals(Registration(
            recordId = "1122144",
            status = RegistrationStatus.REGISTERED,
            registrationId = registrationId,
            aircraftReference = AircraftReference(
                model = "SINUS",
                icaoType = "PISI",
                manufacturer = "PIPISTREL D.O.O.",
                marketingDesignation = null,
                manufactureYear = 2008,
                serialNumber = "246SLCH912",
                maxTakeOffMass = Weight(472, WeightUnit.KILOGRAMS),
                engines = 1,
                transponderCode = TransponderCode.fromOctal("22654061"),
                aircraftType = AircraftType.ECOLIGHT
            ),
            engineReferences = listOf(
                EngineReference(
                    count = 1,
                    manufacturer = "BRP-ROTAX GMBH & CO KG",
                    model = "ROTAX 912 UL"
                )
            ),
            certificateIssueDate = LocalDate.of(2008, 5, 26),
            owner = registrant,
            operator = registrant,
        ), registration)
    }

    @Test
    internal fun swapRegistrantName() {
        assertEquals("First Last", importer.swapRegistrantName("Last, First"))
        assertEquals("First Last", importer.swapRegistrantName("Last,First"))
        assertEquals("First Last", importer.swapRegistrantName("Last,  First"))
        assertEquals("First Last", importer.swapRegistrantName("Last , First"))
        assertEquals("First Last", importer.swapRegistrantName("Last ,First"))
        assertEquals("Last, First, Second", importer.swapRegistrantName("Last, First, Second"))
        assertEquals("", importer.swapRegistrantName(""))
    }

    private fun readFileAsString(filename: String): String {
        val stream = Thread.currentThread().contextClassLoader.getResourceAsStream(filename)
        return stream!!.bufferedReader().use {
            it.readText()
        }
    }
}