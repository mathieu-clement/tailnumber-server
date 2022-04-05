package com.edelweiss.software.tailnumber.server.importer.ch.models.response

@kotlinx.serialization.Serializable
internal data class CategoryNames(
    val de: String,
    val en: String,
    val it: String,
    val fr: String
)
