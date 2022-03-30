package com.edelweiss.software.tailnumber.server.search.elastic.dto.request.search

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class PrefixComponent(
    val value: String,
    @EncodeDefault @SerialName("case_insensitive") val caseInsensitive: Boolean = true
)
