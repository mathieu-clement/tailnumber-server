package com.edelweiss.software.tailnumber.server.importer.ch.models.response

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
internal data class Engine(
    val engineManufacturer: String?,
    val name: String?,
    val count: Int?,
    @Serializable(with = NAorLocalDateTimeSerializer::class)
    val manEndDate: LocalDateTime?
)
