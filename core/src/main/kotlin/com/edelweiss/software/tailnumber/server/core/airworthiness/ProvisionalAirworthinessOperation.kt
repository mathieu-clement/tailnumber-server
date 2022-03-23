package com.edelweiss.software.tailnumber.server.core.airworthiness

import org.slf4j.LoggerFactory

enum class ProvisionalAirworthinessOperation : AirworthinessOperation {
    CLASS1,
    CLASS2;
    
    companion object {
        private val logger = LoggerFactory.getLogger(ProvisionalAirworthinessOperation::class.java)
        
        fun fromFaaCode(code: Int) = when (code) {
            1 -> CLASS1
            2 -> CLASS2
            else -> {
                logger.warn("Unknown provisional airworthiness operation: $code")
                null
            }
        }
    }
}
