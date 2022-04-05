package com.edelweiss.software.tailnumber.server.importer.ch.models.request

import com.edelweiss.software.tailnumber.server.importer.ch.models.ChRegistrationStatus
import kotlinx.serialization.EncodeDefault

@kotlinx.serialization.Serializable
internal data class QueryProperties (
    val registration: String, // e.g. "HB-KOY"
    @EncodeDefault val aircraftStatus: List<ChRegistrationStatus> = ChRegistrationStatus.values().toList()
)