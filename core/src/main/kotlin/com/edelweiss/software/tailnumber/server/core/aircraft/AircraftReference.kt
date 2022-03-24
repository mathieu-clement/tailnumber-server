package com.edelweiss.software.tailnumber.server.core.aircraft

data class AircraftReference(
    val aircraftType: AircraftType?,
    val aircraftCategory: AircraftCategory?,
    val manufacturer: String?,
    val model: String?,
    val typeCertificated: Boolean,
    val engines: Int?,
    val seats: Int?,
    val weightCategory: WeightCategory?,
    val cruisingSpeed: Speed?,
    val numberOfSeats: Int?,
    val manufactureYear: Int?,
    val kitManufacturerName: String?,
    val kitModelName: String?
    )
