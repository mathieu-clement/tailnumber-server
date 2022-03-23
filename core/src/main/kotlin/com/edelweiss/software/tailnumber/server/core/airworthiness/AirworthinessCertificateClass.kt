package com.edelweiss.software.tailnumber.server.core.airworthiness

import org.slf4j.LoggerFactory

enum class AirworthinessCertificateClass : AirworthinessOperation { // implements AirworthinessOperation because of MULTIPLE
    STANDARD,
    LIMITED,
    RESTRICTED,
    EXPERIMENTAL,
    PROVISIONAL,
    MULTIPLE,
    PRIMARY,
    SPECIAL_FLIGHT_PERMIT,
    LIGHT_SPORT;

    companion object {

        private val logger = LoggerFactory.getLogger(AirworthinessCertificateClass::class.java)

        fun fromFaaCode(code: Int) = when (code) {
            1 -> STANDARD
            2 -> LIMITED
            3 -> RESTRICTED
            4 -> EXPERIMENTAL
            5 -> PROVISIONAL
            6 -> MULTIPLE
            7 -> PRIMARY
            8 -> SPECIAL_FLIGHT_PERMIT
            9 -> LIGHT_SPORT
            else -> {
                logger.warn("Unknown certificate class: $code")
                null
            }
        }
    }
}
