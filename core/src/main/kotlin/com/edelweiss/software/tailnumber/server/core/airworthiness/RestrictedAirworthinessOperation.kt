package com.edelweiss.software.tailnumber.server.core.airworthiness

import kotlinx.serialization.Serializable
import org.slf4j.LoggerFactory

@Serializable
enum class RestrictedAirworthinessOperation : AirworthinessOperation {
    OTHER,
    AGRICULTURE_AND_PEST_CONTROL,
    AERIAL_SURVEYING,
    AERIAL_ADVERTISING,
    FOREST,
    PATROLLING,
    WEATHER_CONTROL,
    CARRIAGE_OF_CARGO;
    
    companion object {
        private val logger = LoggerFactory.getLogger(RestrictedAirworthinessOperation::class.java)

        fun fromFaaCodes(codes: String) : List<RestrictedAirworthinessOperation> =
            codes
                .toCharArray()
                .map { it.digitToInt() }
                .mapNotNull { fromFaaCode(it) }
                .toList()
        
        private fun fromFaaCode(code: Int) = when (code) {
            0 -> OTHER
            1 -> AGRICULTURE_AND_PEST_CONTROL
            2 -> AERIAL_SURVEYING
            3 -> AERIAL_ADVERTISING
            4 -> FOREST
            5 -> PATROLLING
            6 -> WEATHER_CONTROL
            7 -> CARRIAGE_OF_CARGO
            else -> {
                logger.warn("Unknown restricted airworthiness operation: $code")
                null
            }
        }
    }
}
