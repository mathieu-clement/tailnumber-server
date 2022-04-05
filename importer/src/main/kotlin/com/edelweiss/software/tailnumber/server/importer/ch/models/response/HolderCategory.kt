package com.edelweiss.software.tailnumber.server.importer.ch.models.response

@kotlinx.serialization.Serializable
internal data class HolderCategory(
    val catId: String,
    val categoryNames: CategoryNames
)
