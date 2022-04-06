package com.edelweiss.software.tailnumber.server.importer.ch.models.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
internal data class Propeller(
    // yep, they made a typo
    @Serializable(with = NAStringSerializer::class) @SerialName("propellerManuafacturer") val propellerManufacturer: String?,
    @Serializable(with = NAStringSerializer::class) val propellerType: String?,
    @Serializable(with = NAIntSerializer::class) val count: Int?,
    @Serializable(with = NAorLocalDateTimeSerializer::class) val manEndDate: LocalDateTime?
)
