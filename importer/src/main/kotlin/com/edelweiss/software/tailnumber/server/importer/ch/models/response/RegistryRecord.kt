package com.edelweiss.software.tailnumber.server.importer.ch.models.response

import com.edelweiss.software.tailnumber.server.importer.ch.models.ChRegistrationStatus
import kotlinx.serialization.Serializable

@Serializable
internal data class RegistryRecord(
    // TODO make some of these Optional
    val lfrId: String?,
    val status: ChRegistrationStatus?,
    val registration: String,
    val aircraftModelType: String?,
    val icaoCode: String?,
    val manufacturer: String?,
    val ownerOperators: List<OwnerOperator>?,
    val details: Details?,
    val aircraftCategories : AircraftCategories?,
    val rescuePdfAvailable: Boolean?
)
