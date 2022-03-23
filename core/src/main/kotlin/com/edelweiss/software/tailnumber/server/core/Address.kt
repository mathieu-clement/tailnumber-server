package com.edelweiss.software.tailnumber.server.core

data class Address (
    val street1: String,
    val street2: String,
    val city: String,
    val state: String,
    val zipCode: String, // TODO dash needs to be added in 5+4 US ZIP codes
    val country: Country
)
