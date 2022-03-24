package com.edelweiss.software.tailnumber.server.core.registration

import com.edelweiss.software.tailnumber.server.core.aircraft.AircraftReference
import com.edelweiss.software.tailnumber.server.core.airworthiness.Airworthiness
import com.edelweiss.software.tailnumber.server.core.engine.EngineReference
import java.time.LocalDate

data class Registration(
    val registrationId: RegistrationId,
    val recordId: String, // there could be multiple records for the same registration number
    val status: RegistrationStatus?,
    val serialNumber: String,
    val aircraftReference: AircraftReference,
    val engineReference: EngineReference?,
    val registrantType: RegistrantType?,
    val registrant: Registrant?,
    val certificateIssueDate: LocalDate?,
    val lastActivityDate: LocalDate?,
    val expirationDate: LocalDate?,
    val airworthiness: Airworthiness?,
    val transponderCode: TransponderCode,
    val fractionalOwnership: Boolean,
    val coOwners: List<String>,
    )
