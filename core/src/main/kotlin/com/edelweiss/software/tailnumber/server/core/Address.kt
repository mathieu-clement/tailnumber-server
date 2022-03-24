package com.edelweiss.software.tailnumber.server.core

import kotlinx.serialization.Serializable

@Serializable
data class Address (
    val street1: String? = null,
    val street2: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zipCode: String? = null,
    val country: String? = null // ISO 3166-1 alpha-2 code. Do not use enum here because value might not be known.
)
