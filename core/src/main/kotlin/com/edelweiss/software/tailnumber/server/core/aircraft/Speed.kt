package com.edelweiss.software.tailnumber.server.core.aircraft

import com.edelweiss.software.tailnumber.server.core.aircraft.SpeedUnit.*

data class Speed(
    val value: Int,
    val unit: SpeedUnit
) {

    private companion object {
        private val MPH_TO_KT = 0.869
        private val MPH_TO_KPH = 1.609
        private val KT_TO_KPH = 1.852
    }

    fun toKnots() : Int = when(unit) {
        KT -> value
        MPH -> (value * MPH_TO_KT).toInt()
        KPH -> (value / KT_TO_KPH).toInt()
    }

    fun toMph() : Int = when (unit) {
        MPH -> value
        KT -> (value / MPH_TO_KT).toInt()
        KPH -> (value / MPH_TO_KPH).toInt()
    }

    fun toKph() : Int = when (unit) {
        KPH -> value
        MPH -> (value * MPH_TO_KPH).toInt()
        KT -> (value * KT_TO_KPH).toInt()
    }
}
