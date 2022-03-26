package com.edelweiss.software.tailnumber.server.search.elastic.dto.response

import com.edelweiss.software.tailnumber.server.core.registration.RegistrationId
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.JsonObject

@kotlinx.serialization.Serializable
data class FieldsResponse(
    @SerialName("registrant.name") val registrantName: List<String>? = null,
    @SerialName("registrant.address") val registrantAddress: JsonObject? = null,
@SerialName("registrationId.id") val registrationId : List<RegistrationId>? = null
)
