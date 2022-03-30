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
    val recordId: String? = null, // there could be multiple records for the same registration number
    val status: RegistrationStatus? = null,
    val aircraftReference: AircraftReference,
    val engineReferences: List<EngineReference> = emptyList(),
    val registrantType: RegistrantType? = null,
    val registrant: StructuredRegistrant? = null, // US registry
    val owner: UnstructuredRegistrant? = null, // CH registry
    val operator: UnstructuredRegistrant? = null, // CH registry
    @Contextual
    val certificateIssueDate: LocalDate? = null,
    @Contextual
    val lastActivityDate: LocalDate? = null,
    @Contextual
    val expirationDate: LocalDate? = null,
    val airworthiness: Airworthiness? = null,
    @Contextual
    val transponderCode: TransponderCode? = null,
    val fractionalOwnership: Boolean = false,
    val coOwners: List<String> = emptyList()
) : Comparable<Registration> {
    override fun compareTo(other: Registration) = this.registrationId.compareTo(other.registrationId)
}