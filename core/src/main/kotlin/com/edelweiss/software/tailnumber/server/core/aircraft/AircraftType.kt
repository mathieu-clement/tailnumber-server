package com.edelweiss.software.tailnumber.server.core.aircraft

import org.slf4j.LoggerFactory

enum class AircraftType {
    GLIDER,
    POWERED_GLIDER,
    BALLOON,
    AIRSHIP, // aka "Blimp/dirigible"
    FIXED_WING_SINGLE_ENGINE,
    FIXED_WING_MULTI_ENGINE,
    HELICOPTER, // FAA calls it "Rotorcraft"
    WEIGHT_SHIFT_CONTROL,
    POWERED_PARACHUTE,
    GYROPLANE,
    ECOLIGHT,
    ULTRALIGHT_GYROPLANE,
    ULTRALIGHT_3_AXIS_CONTROL,
    HYBRID_LIFT,
    TRIKE,
    OTHER;
    companion object {

        private val logger = LoggerFactory.getLogger(AircraftType::class.java)

        fun fromFaaCode(code: Char) = when(code) {
            '1' -> GLIDER
            '2' -> BALLOON
            '3' -> AIRSHIP
            '4' -> FIXED_WING_SINGLE_ENGINE
            '5' -> FIXED_WING_MULTI_ENGINE
            '6' -> HELICOPTER
            '7' -> WEIGHT_SHIFT_CONTROL
            '8' -> POWERED_PARACHUTE
            '9' -> GYROPLANE
            'H' -> HYBRID_LIFT
            'O' -> OTHER
            else -> {
                logger.warn("Unknown aircraft type code: '$code'")
                null
            }
        }
    }
}