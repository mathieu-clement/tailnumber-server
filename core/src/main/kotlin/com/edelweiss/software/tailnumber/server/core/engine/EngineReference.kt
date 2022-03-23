package com.edelweiss.software.tailnumber.server.core.engine

data class EngineReference(
    val engineType: EngineType,
    val manufacturer: String,
    val model: String,
    val power: Power?,
    val thrust: Thrust?,
)
