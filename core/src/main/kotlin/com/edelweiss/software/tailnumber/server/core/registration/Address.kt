package com.edelweiss.software.tailnumber.server.core.registration

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable

@Serializable
data class Address (
    val street1: String? = null,
    val street2: String? = null,
    val poBox: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zipCode: String? = null,
    val zipCode5: String? = zipCode?.takeIf { it.length >= 5 }?.substring(0, 5),
    val country: String? = null, // ISO 3166-1 alpha-2 code in US Registry. Full name in CH registry.
    @EncodeDefault val uniqueId: Int = hashCode()
) {
    val cityAndState = when {
        city != null && state != null -> "$city $state"
        city != null -> city
        state != null -> state
        else -> null
    }

    override fun toString(): String {
        val postZip = listOf(street1, street2, poBox, cityAndState, zipCode, country)
        val preZip = listOf(street1, street2, poBox, zipCode, cityAndState, country)
        val components = if (country == "CH") preZip else postZip
        return components.filterNotNull().joinToString(", ")
    }
}
