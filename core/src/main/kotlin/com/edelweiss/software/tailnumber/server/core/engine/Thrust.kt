package com.edelweiss.software.tailnumber.server.core.engine

import com.edelweiss.software.tailnumber.server.core.engine.ThrustUnit.*

data class Thrust(
    val value: Int,
    val unit: ThrustUnit
) {
    private companion object {
        val NEWTONS_PER_POUND = 4.45
    }

    fun toPounds() : Int = when(unit) {
        POUNDS -> value
        NEWTONS -> (NEWTONS_PER_POUND * value).toInt()
    }

    fun toNewtons(): Int = when (unit) {
        NEWTONS -> value
        POUNDS -> (value / NEWTONS_PER_POUND).toInt()
    }
}

// 4.45 newtons of thrust equals 1 pound of thrust.
// At 550 ft per second, 1 lb of thrust is 1 hp
