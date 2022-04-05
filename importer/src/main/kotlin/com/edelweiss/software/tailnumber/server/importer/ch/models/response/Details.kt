package com.edelweiss.software.tailnumber.server.importer.ch.models.response

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
internal data class Details(
    val marketing: String?,
    val certificationBasis: String?,
    val airworthinessCat: List<String>?,
    val ela: String?,
    val eltCode: EltCode?,
    val yearOfManufacture: Int?,
    val serialNumber: String?,
    val numCrewPax: Int?,
    val minCrew: Int?,
    val mtom: Int?, // maximum take-off mass
    val brs: Boolean?, // "false" => requires isLenient=true
    val engines: List<Engine>?,
    val propellers: List<Propeller>?,
    val noiseClass: String?,
    val noiseStandard: String?,
    val noiseLevel: String?, // "74.8 db(A)"
    @Serializable(with = ArrayDateSerializer::class)
    val regDate: LocalDate?,
    @Serializable(with = ArrayDateSerializer::class)
    val deregDate: LocalDate?,
    val aircraftLegalBasis: String?,
    val tcds: List<String>?,
    val ownership: String?,
    val aircraftAddresses: AircraftAddresses?
)
