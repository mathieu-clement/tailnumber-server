package com.edelweiss.software.tailnumber.server.importer.ch.models.response

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
internal data class Engine(
    @Serializable(with = NAStringSerializer::class) val engineManufacturer: String?,
    @Serializable(with = NAStringSerializer::class) val name: String?,
    @Serializable(with = NAIntSerializer::class) val count: Int?,
    @Serializable(with = NAorLocalDateTimeSerializer::class) val manEndDate: LocalDateTime?
)
