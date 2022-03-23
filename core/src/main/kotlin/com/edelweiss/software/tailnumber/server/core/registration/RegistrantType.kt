package com.edelweiss.software.tailnumber.server.core.registration

import org.slf4j.LoggerFactory

enum class RegistrantType {
    INDIVIDUAL,
    PARTNERSHIP,
    CORPORATION,
    CO_OWNED,
    GOVERNMENT,
    LLC,
    NON_CITIZEN_CORPORATION,
    NON_CITIZEN_CO_OWNED;
    
    companion object {

        private val logger = LoggerFactory.getLogger(RegistrantType::class.java)

        fun fromFaaCode(code: Int) = when(code) {
            1 -> INDIVIDUAL
            2 -> PARTNERSHIP
            3 -> CORPORATION
            4 -> CO_OWNED
            5 -> GOVERNMENT
            7 -> LLC
            8 -> NON_CITIZEN_CORPORATION
            9 -> NON_CITIZEN_CO_OWNED
            else -> {
                logger.warn("Unknown registrant type: $code")
                null
            }
        }
    }
}
