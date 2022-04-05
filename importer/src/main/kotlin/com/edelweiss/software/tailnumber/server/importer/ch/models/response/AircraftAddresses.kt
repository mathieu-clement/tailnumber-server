package com.edelweiss.software.tailnumber.server.importer.ch.models.response

import kotlinx.serialization.Serializable

@Serializable
internal data class AircraftAddresses (
    val dec: String,
    val hex: String,
    val bin: String,
    val oct: String
)
