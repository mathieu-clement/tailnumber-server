package com.edelweiss.software.tailnumber.server.core.aircraft

import com.edelweiss.software.tailnumber.server.core.registration.TransponderCode
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
    val kitModelName: String? = null,
    @Contextual
    val transponderCode: TransponderCode? = null,
    /**
     * Missing fields from CH registry:
     * - airworthinessCat
     * - ela
     * - noiseStandard
     * - tcds (Type Certificate Data Sheet)
     */
    val marketingDesignation: String? = null,
    val certificationBasis: String? = null,
    val minCrew: Int? = null,
    val noiseClass: String? = null,
    val noiseLevel: Double? = null, // db(A)
    val legalBasis: String? = null
)
