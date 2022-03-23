package com.edelweiss.software.tailnumber.server.core.aircraft

import org.slf4j.LoggerFactory

enum class AircraftCategory {
    LAND, SEA, AMPHIBIAN;

    companion object {
        private val logger = LoggerFactory.getLogger(AircraftCategory::class.java)

        fun fromFaaCode(code: Int) = when (code) {
            1 -> LAND
            2 -> SEA
            3 -> AMPHIBIAN
            else -> {
                logger.warn("Unknown aircraft category: $code")
                null
            }
        }
    }
}