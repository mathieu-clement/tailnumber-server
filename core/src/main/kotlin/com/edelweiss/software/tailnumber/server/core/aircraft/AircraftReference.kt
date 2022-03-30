package com.edelweiss.software.tailnumber.server.core.aircraft

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class AircraftReference(
    val aircraftType: AircraftType? = null,
    val aircraftCategory: AircraftCategory? = null,
    val manufacturer: String? = null,
    val model: String? = null,
    val icaoType: String? = null,
    val serialNumber: String? = null,
    val typeCertificated: Boolean? = true,
    val engines: Int? = null,
    val seats: Int? = null, // FAA
    val passengerSeats: Int? = null, // CH
    @Contextual
    val weightCategory: WeightCategory? = null,
    val maxTakeOffMass: Weight? = null,
    val cruisingSpeed: Speed? = null,
    val manufactureYear: Int? = null,
    val kitManufacturerName: String? = null,
    val kitModelName: String? = null
)
