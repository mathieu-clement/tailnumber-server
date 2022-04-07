package com.edelweiss.software.tailnumber.server.core.aircraft

import kotlinx.serialization.Serializable

@Serializable
data class PropellerReference(
    val manufacturer: String? = null,
    val model: String? = null,
    val count: Int? = null
)
