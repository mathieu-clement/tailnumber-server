package com.edelweiss.software.tailnumber.server.core.registration

import com.edelweiss.software.tailnumber.server.core.aircraft.AircraftReference
import com.edelweiss.software.tailnumber.server.core.airworthiness.Airworthiness
import com.edelweiss.software.tailnumber.server.core.engine.EngineReference
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Registration(
    val registrationId: RegistrationId,
    val recordId: String, // there could be multiple records for the same registration number
    val status: RegistrationStatus? = null,
    val serialNumber: String,
    val aircraftReference: AircraftReference,
    val engineReference: EngineReference? = null,
    val registrantType: RegistrantType? = null,
    val registrant: Registrant? = null,
    @Contextual
    val certificateIssueDate: LocalDate? = null,
    @Contextual
    val lastActivityDate: LocalDate? = null,
    @Contextual
    val expirationDate: LocalDate? = null,
    val airworthiness: Airworthiness? = null,
    val transponderCode: TransponderCode,
    val fractionalOwnership: Boolean,
    val coOwners: List<String> = emptyList(),
    )
