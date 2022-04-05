package com.edelweiss.software.tailnumber.server.importer.ch.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class ChRegistrationStatus {
    @SerialName("Registered") REGISTERED,
    @SerialName("Reserved") RESERVED,
    @SerialName("Reservation Expired") EXPIRED,
    @SerialName("Registration in Progress") IN_PROGRESS
}