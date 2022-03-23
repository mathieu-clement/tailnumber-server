package com.edelweiss.software.tailnumber.server.core.airworthiness

import org.slf4j.LoggerFactory

enum class StandardAirworthinessOperation : AirworthinessOperation {
    NORMAL,
    UTILITY,
    ACROBATIC,
    TRANSPORT,
    GLIDER,
    BALLOON,
    COMMUTER,
    OTHER;
    
    companion object {
        private val logger = LoggerFactory.getLogger(StandardAirworthinessOperation::class.java)
        
        fun fromFaaCode(code: Char) = when (code) {
            'N' -> NORMAL
            'U' -> UTILITY
            'A' -> ACROBATIC
            'T' -> TRANSPORT
            'G' -> GLIDER
            'B' -> BALLOON
            'C' -> COMMUTER
            'O' -> OTHER
            else -> {
                logger.warn("Unknown standard airworthiness operation: $code")
                null
            }
        }
    }
}
