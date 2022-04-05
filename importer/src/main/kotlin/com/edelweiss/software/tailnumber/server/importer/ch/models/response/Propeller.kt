package com.edelweiss.software.tailnumber.server.importer.ch.models.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
internal data class Propeller(
    // yep, they made a typo
    @SerialName("propellerManuafacturer") val propellerManufacturer: String?,
    val propellerType: String?,
    val count: Int?,
    @Serializable(with = NAorLocalDateTimeSerializer::class)
    val manEndDate: LocalDateTime?
)
