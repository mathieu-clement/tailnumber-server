package com.edelweiss.software.tailnumber.server.search.elastic.dto.response

import com.edelweiss.software.tailnumber.server.core.registration.Registrant
import com.edelweiss.software.tailnumber.server.core.registration.RegistrationId

data class PartialRegistration(
    val registrationId: RegistrationId,
    val manufacturer: String?,
    val model: String?,
    val year: Int?,
    val registrant: Registrant
)
