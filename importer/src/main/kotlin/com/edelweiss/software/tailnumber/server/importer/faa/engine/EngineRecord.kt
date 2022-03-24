package com.edelweiss.software.tailnumber.server.importer.faa.engine

data class EngineRecord(
    val code: String,
    val mfr: String,
    val model: String,
    val type: Int?,
    val horsepower: Int?,
    val thrust: Int?
)
