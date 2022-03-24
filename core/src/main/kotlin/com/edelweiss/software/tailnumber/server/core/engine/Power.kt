package com.edelweiss.software.tailnumber.server.core.engine

import com.edelweiss.software.tailnumber.server.core.engine.PowerUnit.*
import kotlinx.serialization.Serializable

@Serializable
data class Power(
    val value: Int,
    val unit: PowerUnit
) {
    private companion object {
        const val SAE_TO_METRIC_HP = 1.014
        const val METRIC_HP_TO_WATTS = 735.5
        const val SAE_HP_TO_WATTS = 745.7
    }

    fun toMetricHP() : Int = when (unit) {
        METRIC_HP -> value
        SAE_HP -> (value * SAE_TO_METRIC_HP).toInt()
        WATTS -> (value / METRIC_HP_TO_WATTS).toInt()
    }

    fun toSaeHP() : Int = when (unit) {
        SAE_HP -> value
        METRIC_HP -> (value / SAE_TO_METRIC_HP).toInt()
        WATTS -> (value / SAE_HP_TO_WATTS).toInt()
    }

    fun toWatts() : Int = when (unit) {
        WATTS -> value
        SAE_HP -> (value * SAE_HP_TO_WATTS).toInt()
        METRIC_HP -> (value * METRIC_HP_TO_WATTS).toInt()
    }
}
