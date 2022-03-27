package com.edelweiss.software.tailnumber.server.importer.ch

import com.edelweiss.software.tailnumber.server.core.Address
import com.edelweiss.software.tailnumber.server.core.Country
import com.edelweiss.software.tailnumber.server.core.aircraft.Weight
import com.edelweiss.software.tailnumber.server.core.aircraft.WeightUnit
import com.edelweiss.software.tailnumber.server.core.registration.Registration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChRegistrationSummaryHtmlParserTest {

    private val parser = ChRegistrationSummaryHtmlParser()
    private val registrations: Map<String, Registration> = parser.import().associateBy { it.registrationId.id }

    @Test
    fun kow() {

    }

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
        
        mainOwnerName: String,
        mainOwnerStreet1: String,
        mainOwnerStreet2: String,
        mainOwnerZipCode: Int,
        mainOwnerCity: String,

        mainOperatorName: String = mainOwnerName,
        mainOperatorStreet1: String = mainOwnerStreet1,
        mainOperatorStreet2: String = mainOwnerStreet2,
        mainOperatorZipCode: Int = mainOwnerZipCode,
        mainOperatorCity: String = mainOwnerCity,
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
        
        assertEquals(mainOwnerName, r.owner?.name)
        assertEquals(Address(
            mainOwnerStreet1,
            mainOwnerStreet2,
            mainOwnerCity,
            null,
            mainOwnerZipCode.toString(),
            "Switzerland"
        ), r.owner?.address)

        assertEquals(mainOperatorName, r.operator?.name)
        assertEquals(Address(
            mainOperatorStreet1,
            mainOperatorStreet2,
            mainOperatorCity,
            null,
            mainOperatorZipCode.toString(),
            "Switzerland"
        ), r.operator?.address)
    }
}