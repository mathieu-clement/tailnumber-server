package com.edelweiss.software.tailnumber.server.core.registration

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable

@Serializable
data class Registrant(
    val name: String? = null,
    val address: Address? = null,
    @EncodeDefault val uniqueId: Int = hashCode()
)
