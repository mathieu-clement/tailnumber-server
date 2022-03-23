package com.edelweiss.software.tailnumber.server.core.aircraft

import com.edelweiss.software.tailnumber.server.core.registration.Weight

data class AircraftReference(
    val aircraftType: AircraftType,
    val aircraftCategory: AircraftCategory,
    val manufacturer: String,
    val model: String,
    val certificationType: CertificationType,
    val engines: Int,
    val seats: Int,
    val weightCategory: WeightCategory,
    val cruisingSpeedKts: Int,
    val numberOfSeats: Int,
    val manufactureYear: Int,
    val kitManufacturerName: String?,
    val kitModelName: String?
    )
