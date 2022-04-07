package com.edelweiss.software.tailnumber.server.importer.ch.models.response

import kotlinx.serialization.Serializable

@Serializable
internal data class ChAddress(
    @Serializable(with = NAStringSerializer::class)
    val city: String?,
    @Serializable(with = NAStringSerializer::class)
    val country: String?,
    @Serializable(with = NAIntSerializer::class)
    val poBox: Int?,
    @Serializable(with = NAStringSerializer::class)
    val poBoxName: String?, // "Boîte postale", "Case postale", "Casella postale", "P.O. Box", "PO Box"  or "N/A", null
    @Serializable(with = NAStringSerializer::class)
    val street: String?,
    @Serializable(with = NAStringSerializer::class)
    val streetNo: String?,
    @Serializable(with = NAStringSerializer::class)
    val extraLine: String?,
    @Serializable(with = NAStringSerializer::class)
    val zipCode: String?,
    val empty: Boolean?
)
