package com.edelweiss.software.tailnumber.server.importer.ch.models.response

import com.edelweiss.software.tailnumber.server.importer.ch.models.ChRegistrationStatus
import kotlinx.serialization.Serializable

@Serializable
internal data class RegistryRecord(
    // TODO make some of these Optional
    val lfrId: String?,
    val status: ChRegistrationStatus?,
    val registration: String,
    @Serializable(with = NAStringSerializer::class) val aircraftModelType: String?,
    @Serializable(with = NAStringSerializer::class) val icaoCode: String?,
    @Serializable(with = NAStringSerializer::class) val manufacturer: String?,
    val ownerOperators: List<OwnerOperator>?,
    val details: Details?,
    val aircraftCategories : AircraftCategories?,
    val rescuePdfAvailable: Boolean?
)
