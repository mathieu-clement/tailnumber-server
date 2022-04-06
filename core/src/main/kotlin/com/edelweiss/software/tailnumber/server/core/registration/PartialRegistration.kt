package com.edelweiss.software.tailnumber.server.core.registration

import kotlinx.serialization.Serializable

@Serializable
data class PartialRegistration(
    val registrationId: RegistrationId,
    val manufacturer: String? = null,
    val model: String? = null,
    val year: Int? = null,
    val registrant: Registrant? = null,
    val owner: Registrant? = null,
    val operator: Registrant? = null
)
