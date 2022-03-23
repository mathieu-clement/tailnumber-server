package com.edelweiss.software.tailnumber.server.core.airworthiness

import org.slf4j.LoggerFactory

enum class LightSportAirworthinessOperation : AirworthinessOperation {
    AIRPLANE,
    GLIDER,
    LIGHTER_THAN_AIR,
    POWERED_PARACHUTE,
    WEIGHT_SHIFT_CONTROL
    ;
    
    companion object {
        private val logger = LoggerFactory.getLogger(LightSportAirworthinessOperation::class.java)
        
        fun fromFaaCode(code: Char) = when (code) {
            'A' -> AIRPLANE
            'G' -> GLIDER
            'L' -> LIGHTER_THAN_AIR
            'P' -> POWERED_PARACHUTE
            'W' -> WEIGHT_SHIFT_CONTROL
            else -> {
                logger.warn("Unknown light sport airworthiness operation: $code")
                null
            }
        }
    }
}
