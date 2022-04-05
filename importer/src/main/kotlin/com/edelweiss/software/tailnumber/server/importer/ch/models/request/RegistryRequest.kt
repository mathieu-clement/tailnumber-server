package com.edelweiss.software.tailnumber.server.importer.ch.models.request

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
internal data class RegistryRequest(
    val queryProperties: QueryProperties,
    @EncodeDefault val language: String = "en",
    @EncodeDefault @SerialName("sort_list") val sort : String = "registration",
    @EncodeDefault @SerialName("current_page_number") val page: Int = 1,
    @EncodeDefault @SerialName("page_result_limit") val limit: Int = 100
)
