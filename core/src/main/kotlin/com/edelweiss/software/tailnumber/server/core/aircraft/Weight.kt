package com.edelweiss.software.tailnumber.server.core.aircraft

import com.edelweiss.software.tailnumber.server.core.aircraft.WeightUnit.KILOGRAMS
import com.edelweiss.software.tailnumber.server.core.aircraft.WeightUnit.US_POUNDS

fun Int.lbs() = Weight(this, US_POUNDS)

@kotlinx.serialization.Serializable
data class Weight(
    val value: Int,
    val unit: WeightUnit
) {
    fun toKg() = when (unit) {
        KILOGRAMS -> value
        US_POUNDS -> (value / 2.2).toInt()
    }

    fun toUsPounds() = when (unit) {
        US_POUNDS -> value
        KILOGRAMS -> (value * 2.2).toInt()
    }
}