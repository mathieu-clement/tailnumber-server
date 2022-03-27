package com.edelweiss.software.tailnumber.server.core.registration

import kotlinx.serialization.Serializable

@Serializable
data class PartialRegistration(
    val registrationId: RegistrationId,
    val manufacturer: String?,
    val model: String?,
    val year: Int?,
    val registrant: StructuredRegistrant
)
