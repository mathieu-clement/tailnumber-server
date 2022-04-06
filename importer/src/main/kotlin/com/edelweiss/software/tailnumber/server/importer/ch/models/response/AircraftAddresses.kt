package com.edelweiss.software.tailnumber.server.importer.ch.models.response

import kotlinx.serialization.Serializable

@Serializable
internal data class AircraftAddresses(
    @Serializable(with = NAStringSerializer::class) val dec: String?,
    @Serializable(with = NAStringSerializer::class) val hex: String?,
    @Serializable(with = NAStringSerializer::class) val bin: String?,
    @Serializable(with = NAStringSerializer::class) val oct: String?
)
