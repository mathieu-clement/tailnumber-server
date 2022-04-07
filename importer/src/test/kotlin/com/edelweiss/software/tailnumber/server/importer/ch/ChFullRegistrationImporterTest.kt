package com.edelweiss.software.tailnumber.server.importer.ch

import com.edelweiss.software.tailnumber.server.core.Country
import com.edelweiss.software.tailnumber.server.core.aircraft.*
import com.edelweiss.software.tailnumber.server.core.engine.EngineReference
import com.edelweiss.software.tailnumber.server.core.registration.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class ChFullRegistrationImporterTest {

    private val importer = ChFullRegistrationImporter("json.tar.gz", false)

    @Test
    fun import() {
        importer.import()
    }

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
                aircraftType = AircraftType.ECOLIGHT,
                passengerSeats = 1,
                minCrew = 1,
                noiseClass = "D",
                noiseLevel = 61.2,
                legalBasis = "Non-EASA",
                certificationBasis = "LTF-UL & FOCA additional requirements"
            ),
            engineReferences = listOf(
                EngineReference(
                    count = 1,
                    manufacturer = "BRP-ROTAX GMBH & CO KG",
                    model = "ROTAX 912 UL"
                )
            ),
            propellerReferences = listOf(
                PropellerReference(
                    count = 1,
                    manufacturer = "PIPISTREL D.O.O.",
                    model = "VARIO"
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

    @Test
    fun noiseLevel() {
        assertEquals(84.4, importer.parseNoiseLevel("SEL: 84.4"))
        assertEquals(91.3, importer.parseNoiseLevel("91.3 dB(A)"))
    }

    private fun readFileAsString(filename: String): String {
        val stream = Thread.currentThread().contextClassLoader.getResourceAsStream(filename)
        return stream!!.bufferedReader().use {
            it.readText()
        }
    }
}