package com.edelweiss.software.tailnumber.server.search.elastic.dto.request.search

import com.edelweiss.software.tailnumber.server.core.Country
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class MatchQuery(
    @SerialName("registrant.name") val registrantName: String? = null,
    @SerialName("registrationId.country") val registrationCountry: Country? = null
)
