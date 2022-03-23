package com.edelweiss.software.tailnumber.server.core.registration

import com.edelweiss.software.tailnumber.server.core.registration.WeightUnit.*

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