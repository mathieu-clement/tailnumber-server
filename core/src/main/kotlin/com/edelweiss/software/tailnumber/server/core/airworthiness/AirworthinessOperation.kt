package com.edelweiss.software.tailnumber.server.core.airworthiness

import com.edelweiss.software.tailnumber.server.core.airworthiness.AirworthinessCertificateClass.*

interface AirworthinessOperation {
    // Members are same as enum
    val name: String
    val ordinal: Int

    companion object {

        fun fromFaaCode(code: String, certificateClass: AirworthinessCertificateClass): List<AirworthinessOperation> =
            if (code.isEmpty()) emptyList()
            else
                code.trim().let { trimmedCode ->
                    when (certificateClass) {
                        STANDARD -> StandardAirworthinessOperation.fromFaaCode(trimmedCode[0]).toList()
                        LIMITED -> emptyList()
                        RESTRICTED -> RestrictedAirworthinessOperation.fromFaaCodes(trimmedCode)
                        EXPERIMENTAL -> ExperimentalAirworthinessOperation.fromFaaCodes(trimmedCode)
                        PROVISIONAL -> ProvisionalAirworthinessOperation.fromFaaCode(trimmedCode.toInt()).toList()
                        MULTIPLE -> fromMultiple(trimmedCode)
                        PRIMARY -> emptyList()
                        SPECIAL_FLIGHT_PERMIT -> SpecialFlightPermitAirworthinessOperation.fromFaaCodes(trimmedCode)
                        LIGHT_SPORT -> LightSportAirworthinessOperation.fromFaaCode(trimmedCode[0]).toList()
                    }
                }

        private fun fromMultiple(code: String): MutableList<AirworthinessOperation> {
            val trimmed = code.trim()
            if (code.length < 2) emptyList<AirworthinessOperation>()

            val output: MutableList<AirworthinessOperation> = trimmed.substring(0, 2)
                .toCharArray()
                .map { it.digitToInt() }
                .mapNotNull { AirworthinessCertificateClass.fromFaaCode(it) }
                .toMutableList()

            if (code.length > 2) {
                output += MultipleAirworthinessOperation.fromFaaCodes(trimmed.substring(2))
            }

            return output
        }

        private fun AirworthinessOperation?.toList(): List<AirworthinessOperation> =
            if (this == null) emptyList()
            else listOf(this)
    }
}