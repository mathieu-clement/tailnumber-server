package com.edelweiss.software.tailnumber.server.core.engine

import kotlinx.serialization.Serializable

@Serializable
data class EngineReference(
    val engineType: EngineType? = null,
    val manufacturer: String,
    val model: String,
    val power: Power? = null,
    val thrust: Thrust? = null
)
