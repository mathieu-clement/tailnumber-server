package com.edelweiss.software.tailnumber.server.core.registration

import com.edelweiss.software.tailnumber.server.core.Country
import com.edelweiss.software.tailnumber.server.core.exceptions.CountryNotFoundException
import kotlinx.serialization.Serializable

//@Serializable(with = RegistrationIdSerializer::class)
@Serializable
data class RegistrationId(
    val id: String,
    val country: Country
) : Comparable<RegistrationId> {
    companion object {
        fun fromTailNumber(rawId: String) : RegistrationId {
            val sanitizedId = sanitize(rawId)
            if (sanitizedId.startsWith("N"))
                return RegistrationId(sanitizedId, Country.US)
            else if (sanitizedId.startsWith("HB")) {
                return RegistrationId(sanitizedId, Country.CH)
            }
            throw CountryNotFoundException(rawId)
        }

        fun sanitize(tailNumber: String): String {
            val sanitized = tailNumber.replace(" ", "").uppercase().trim()
            return when {
                sanitized.startsWith("HB") -> "HB-" + sanitized.substring(2)
                sanitized.startsWith("N") -> "N-" + sanitized.substring(1)
                else -> throw CountryNotFoundException("$sanitized*")
            }
        }
    }

    override fun toString() = "$id (${country.name})"
    override fun compareTo(other: RegistrationId) = id.compareTo(other.id)
}
