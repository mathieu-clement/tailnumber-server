package com.edelweiss.software.tailnumber.server.core.registration

import kotlinx.serialization.Serializable

@Serializable
data class StructuredRegistrant(
    val name: String? = null,
    val address: Address? = null,
) : Registrant()
