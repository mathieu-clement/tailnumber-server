package com.edelweiss.software.tailnumber.server.core.registration

import com.edelweiss.software.tailnumber.server.core.Address

import kotlinx.serialization.Serializable

@Serializable
data class Registrant(
    val name: String? = null,
    val address: Address? = null
)
