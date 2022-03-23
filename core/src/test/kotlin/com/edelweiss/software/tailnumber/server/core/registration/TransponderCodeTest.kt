package com.edelweiss.software.tailnumber.server.core.registration

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class TransponderCodeTest {

    val HEX = "A05DFE"
    val OCTAL = "50056776"

    @Test
    fun toHex() {
        assertEquals(HEX, TransponderCode.fromHex(HEX).hex())
        assertEquals(HEX, TransponderCode.fromOctal(OCTAL).hex())
    }

    @Test
    fun toOctal() {
        assertEquals(OCTAL, TransponderCode.fromOctal(OCTAL).octal())
        assertEquals(OCTAL, TransponderCode.fromHex(HEX).octal())
    }
}