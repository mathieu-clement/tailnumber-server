package com.edelweiss.software.tailnumber.server.api.models

import kotlinx.serialization.Serializable

@Serializable
class NotFoundErrorDTO(
    val type: String,
    val tailNumber: String
)