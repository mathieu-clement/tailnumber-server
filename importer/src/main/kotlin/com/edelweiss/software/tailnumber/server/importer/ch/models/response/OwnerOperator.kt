package com.edelweiss.software.tailnumber.server.importer.ch.models.response

import kotlinx.serialization.Serializable

@Serializable
internal data class OwnerOperator(
    val holderId: String?,
    val holderCategory: HolderCategory,
    val ownerOperator: String?,
    val address: ChAddress?
)
