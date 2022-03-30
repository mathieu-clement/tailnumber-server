package com.edelweiss.software.tailnumber.server.search.elastic.dto.request.search

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class PrefixQuery(
    @SerialName("registrationId.id") val registrationIdId : String? = null
)
