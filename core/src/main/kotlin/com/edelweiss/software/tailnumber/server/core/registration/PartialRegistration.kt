package com.edelweiss.software.tailnumber.server.core.registration

import kotlinx.serialization.Serializable

@Serializable
data class PartialRegistration(
    val registrationId: RegistrationId,
    val manufacturer: String? = null,
    val model: String? = null,
    val year: Int? = null,
    val registrant: StructuredRegistrant? = null,
    val owner: UnstructuredRegistrant? = null,
    val operator: UnstructuredRegistrant? = null
)
