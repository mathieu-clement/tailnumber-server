package com.edelweiss.software.tailnumber.server.importer.ch.models.response

import kotlinx.serialization.Serializable

@Serializable
internal data class AircraftCategoryTranslation(
    val catId: String,
    val text: String,
    val lang: String // "en"
)
