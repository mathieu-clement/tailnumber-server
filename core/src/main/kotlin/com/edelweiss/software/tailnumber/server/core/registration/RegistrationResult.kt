package com.edelweiss.software.tailnumber.server.core.registration

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class RegistrationResult(
    @Contextual
    val lastUpdate: LocalDate?,
    val registration: Registration
)
