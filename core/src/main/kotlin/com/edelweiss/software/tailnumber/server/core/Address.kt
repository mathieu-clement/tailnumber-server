package com.edelweiss.software.tailnumber.server.core

data class Address (
    val street1: String?,
    val street2: String?,
    val city: String?,
    val state: String?,
    val zipCode: String?,
    val country: String? // ISO 3166-1 alpha-2 code. Do not use enum here because value might not be known.
)
