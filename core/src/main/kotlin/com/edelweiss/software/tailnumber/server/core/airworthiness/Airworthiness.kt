package com.edelweiss.software.tailnumber.server.core.airworthiness

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Airworthiness(
    val certificateClass: AirworthinessCertificateClass? = null,
    val approvedOperation: List<AirworthinessOperation> = emptyList(),
    @Contextual
    val airworthinessDate: LocalDate?
)
