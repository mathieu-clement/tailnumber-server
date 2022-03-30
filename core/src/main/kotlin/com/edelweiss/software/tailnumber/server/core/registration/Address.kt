package com.edelweiss.software.tailnumber.server.core.registration

import kotlinx.serialization.Serializable

@Serializable
data class Address (
    val street1: String? = null,
    val street2: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zipCode: String? = null,
    val zipCode5: String? = zipCode?.takeIf { it.length >= 5 }?.substring(0, 5),
    val country: String? = null, // ISO 3166-1 alpha-2 code. Do not use enum here because value might not be known.
    val uniqueId: Int = hashCode()
)
