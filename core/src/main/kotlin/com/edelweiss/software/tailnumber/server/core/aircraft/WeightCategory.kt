package com.edelweiss.software.tailnumber.server.core.aircraft

import org.slf4j.LoggerFactory

enum class WeightCategory(val min: Int, val max: Int?) {
    CLASS1(0, 12499),
    CLASS2(12500, 19999),
    CLASS3(20000, null),
    UAV(0, 55) // Unmanned aerial vehicle AKA drone
    ;

    companion object {
        private val logger = LoggerFactory.getLogger(WeightCategory::class.java)

        fun fromFaaCode(code: Int) = when(code) {
            1 -> CLASS1
            2 -> CLASS2
            3 -> CLASS3
            4 -> UAV
            else -> {
                logger.warn("Unknown weight category: $code")
                null
            }
        }
    }
}