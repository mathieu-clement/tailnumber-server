package com.edelweiss.software.tailnumber.server.core.registration

import com.edelweiss.software.tailnumber.server.core.Country
import com.edelweiss.software.tailnumber.server.core.exceptions.CountryNotFoundException
import com.edelweiss.software.tailnumber.server.core.serializers.RegistrationIdSerializer

import kotlinx.serialization.Serializable

@Serializable(with = RegistrationIdSerializer::class)
data class RegistrationId(
    val id: String,
    val country: Country
) : Comparable<RegistrationId> {
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
            throw CountryNotFoundException(rawId)
        }
    }

    override fun toString() = "$id (${country.name})"
    override fun compareTo(other: RegistrationId) = id.compareTo(other.id)
}
