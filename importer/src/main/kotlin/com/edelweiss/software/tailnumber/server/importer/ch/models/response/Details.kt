package com.edelweiss.software.tailnumber.server.importer.ch.models.response

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
internal data class Details(
    @Serializable(with = NAStringSerializer::class) val marketing: String?,
    @Serializable(with = NAStringSerializer::class) val certificationBasis: String?,
    val airworthinessCat: List<String>?,
    @Serializable(with = NAStringSerializer::class) val ela: String?,
//    val eltCode: EltCode?, // appears unused
    @Serializable(with = NAIntSerializer::class) val yearOfManufacture: Int?,
    @Serializable(with = NAStringSerializer::class) val serialNumber: String?,
    @Serializable(with = NAIntSerializer::class) val numCrewPax: Int?,
    @Serializable(with = NAIntSerializer::class) val minCrew: Int?,
    val mtom: Double?, // maximum take-off mass
    val brs: Boolean?, // "false" => requires isLenient=true, Ballistic Recovery System
    val engines: List<Engine>?,
    val propellers: List<Propeller>?,
    @Serializable(with = NAStringSerializer::class) val noiseClass: String?,
    @Serializable(with = NAStringSerializer::class) val noiseStandard: String?,
    @Serializable(with = NAStringSerializer::class) val noiseLevel: String?, // "74.8 db(A)"
    @Serializable(with = ArrayDateSerializer::class)
    val regDate: LocalDate?,
//    @Serializable(with = ArrayDateSerializer::class) val deregDate: LocalDate?, // appears unused
    @Serializable(with = NAStringSerializer::class) val aircraftLegalBasis: String?,
    val tcds: List<String>?, // Type Certificate Data Sheet
    @Serializable(with = NAStringSerializer::class) val ownership: String?,
    val aircraftAddresses: AircraftAddresses?
)
