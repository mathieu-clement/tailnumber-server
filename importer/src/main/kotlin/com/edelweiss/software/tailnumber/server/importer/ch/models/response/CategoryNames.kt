package com.edelweiss.software.tailnumber.server.importer.ch.models.response

@kotlinx.serialization.Serializable
internal data class CategoryNames(
    val de: String? = null,
    val en: String? = null,
    val it: String? = null,
    val fr: String? = null
)
