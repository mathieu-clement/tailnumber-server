package com.edelweiss.software.tailnumber.server.core.aircraft

import kotlinx.serialization.Serializable

@Serializable
data class AircraftReference(
    val aircraftType: AircraftType? = null,
    val aircraftCategory: AircraftCategory? = null,
    val manufacturer: String? = null,
    val model: String? = null,
    val typeCertificated: Boolean,
    val engines: Int? = null,
    val seats: Int? = null,
    val weightCategory: WeightCategory? = null,
    val cruisingSpeed: Speed? = null,
    val numberOfSeats: Int? = null,
    val manufactureYear: Int? = null,
    val kitManufacturerName: String? = null,
    val kitModelName: String? = null
    )
