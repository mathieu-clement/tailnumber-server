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
    val recordId: String?, // there could be multiple records for the same registration number
    val status: RegistrationStatus? = null,
    val aircraftReference: AircraftReference,
    val engineReference: EngineReference? = null,
    val registrantType: RegistrantType? = null,
    val owner: Registrant? = null, // Owner in CH registry, "registrant" in US registry
    val operator: Registrant? = null, // CH registry only
    @Contextual
    val certificateIssueDate: LocalDate? = null,
    @Contextual
    val lastActivityDate: LocalDate? = null,
    @Contextual
    val expirationDate: LocalDate? = null,
    val airworthiness: Airworthiness? = null,
    @Contextual
    val transponderCode: TransponderCode?,
    val fractionalOwnership: Boolean = false,
    val coOwners: List<String> = emptyList()
) : Comparable<Registration> {
    override fun compareTo(other: Registration) = this.registrationId.compareTo(other.registrationId)
}