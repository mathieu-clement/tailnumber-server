package com.edelweiss.software.tailnumber.server.core.registration

import com.edelweiss.software.tailnumber.server.core.Country

data class RegistrationId(
    val id: String,
    val country: Country
) {
    companion object {
        fun fromTailNumber(rawId: String) : RegistrationId {
            val sanitizedId = rawId
                .replace(" ", "")
                .replace("-", "")
            if (rawId.startsWith("N"))
                return RegistrationId(sanitizedId, Country.US)
            else if (rawId.startsWith("HB")) {
                return RegistrationId(sanitizedId, Country.CH)
            }
            throw IllegalArgumentException("Unknown registration country")
        }
    }
}
