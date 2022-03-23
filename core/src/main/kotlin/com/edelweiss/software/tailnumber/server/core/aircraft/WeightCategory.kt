package com.edelweiss.software.tailnumber.server.core.aircraft

enum class WeightCategory(val min: Int, val max: Int?) {
    CLASS1(0, 12499),
    CLASS2(12500, 19999),
    CLASS3(20000, null),
    UAV(0, 55) // Unmanned aerial vehicle AKA drone
}