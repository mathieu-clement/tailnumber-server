package com.edelweiss.software.tailnumber.server.importer.ch.models.response

import kotlinx.serialization.Serializable

@Serializable
internal data class AircraftCategories(
    val catId: String,
    val aircraftCategoryTranslations: List<AircraftCategoryTranslation>
)
