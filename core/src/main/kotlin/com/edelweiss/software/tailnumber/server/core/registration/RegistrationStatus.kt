package com.edelweiss.software.tailnumber.server.core.registration

import org.slf4j.LoggerFactory

enum class RegistrationStatus {
    VALID,
    PENDING,
    INVALID,
    ASSIGNED_BUT_NOT_REGISTERED,
    RESERVED,
    CANCELLED,
    REVOKED,
    EXPIRED,
    SALE_REPORTED
    ;

    companion object {

        private val logger = LoggerFactory.getLogger(RegistrationStatus::class.java)

        fun fromFaaCode(code: String) : RegistrationStatus? = when (code) {
            "A", "M", "N", "S", "T", "V", "X", "1", "8", "14", "15", "24", "25", "26", "28" -> VALID
            "D", "27" -> EXPIRED
            "E" -> REVOKED
            "R", "19" -> PENDING
            "W" -> INVALID
            "Z", "5" -> RESERVED
            "2", "3", "4", "10", "11", "12" -> ASSIGNED_BUT_NOT_REGISTERED
            "6", "20", "22" -> CANCELLED
            "7", "17", "18" -> SALE_REPORTED
            "9", "21" -> REVOKED
            "13", "16", "23", "29" -> EXPIRED
            else -> {
                logger.warn("Unknown registration status: $code")
                null
            }
        }
    }
}