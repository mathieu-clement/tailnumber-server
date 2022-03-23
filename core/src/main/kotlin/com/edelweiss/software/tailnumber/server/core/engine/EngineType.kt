package com.edelweiss.software.tailnumber.server.core.engine

import org.slf4j.LoggerFactory

enum class EngineType {
    NONE,
    RECIPROCATING,
    TURBO_PROP,
    TURBO_SHAFT,
    TURBO_JET,
    TURBO_FAN,
    RAMJET,
    TWO_CYCLE,
    FOUR_CYCLE,
    UNKNOWN,
    ELECTRIC,
    ROTARY
    ;

    companion object {
        private val logger = LoggerFactory.getLogger(EngineType::class.java)

        fun fromFaaCode(code: Int) = when (code) {
            0 -> NONE
            1 -> RECIPROCATING
            2 -> TURBO_PROP
            3 -> TURBO_SHAFT
            4 -> TURBO_JET
            5 -> TURBO_FAN
            6 -> RAMJET
            7 -> TWO_CYCLE
            8 -> FOUR_CYCLE
            9 -> UNKNOWN
            10 -> ELECTRIC
            11 -> ROTARY
            else -> {
                logger.warn("Unknown engine type: $code")
                null
            }
        }
    }
}