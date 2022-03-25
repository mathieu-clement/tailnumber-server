package com.edelweiss.software.tailnumber.server.core.aircraft

import org.slf4j.LoggerFactory

@kotlinx.serialization.Serializable
enum class WeightCategory(val minWeight: Weight, val maxWeight: Weight?) {
    CLASS1(0.lbs(), 12499.lbs()),
    CLASS2(12500.lbs(), 19999.lbs()),
    CLASS3(20000.lbs(), null),
    UAV(0.lbs(), 55.lbs()) // Unmanned aerial vehicle AKA drone
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