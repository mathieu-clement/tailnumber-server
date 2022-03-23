package com.edelweiss.software.tailnumber.server.core.airworthiness

import com.edelweiss.software.tailnumber.server.core.airworthiness.ExperimentalAirworthinessOperation.Companion.tokenize
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ExperimentalAirworthinessOperationTest {
    @Test
    fun testTokenize() {
        assertEquals(emptyList<String>(), tokenize("").toList())
        assertEquals(listOf("4"), tokenize("4").toList())
        assertEquals(listOf("4", "0"), tokenize("40").toList())
        assertEquals(listOf("4", "1", "5", "6", "0"), tokenize("41560").toList())
        assertEquals(listOf("4", "1", "5", "6", "9A"), tokenize("41569A").toList())
        assertEquals(listOf("4", "1", "5", "6", "9A", "3", "8D"), tokenize("41569A38D").toList())
        assertEquals(listOf("4", "1", "5", "6", "9A", "3", "8D", "7"), tokenize("41569A38D7").toList())
        assertEquals(listOf("4", "1", "5", "6", "9A"), tokenize("41569A ").toList())
        assertEquals(listOf("4", "9A", "9C", "9E"), tokenize("49A9C9E").toList())
    }
}