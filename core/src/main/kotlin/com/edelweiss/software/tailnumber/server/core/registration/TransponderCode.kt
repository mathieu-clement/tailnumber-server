package com.edelweiss.software.tailnumber.server.core.registration

data class TransponderCode(val code: Long) {

    companion object {
        fun fromHex(hex: String) = TransponderCode(hex.toLong(16))
        fun fromOctal(octal: String) = TransponderCode(octal.toLong(8))
    }

    fun hex() = code.toString(16).uppercase()

    fun octal() = code.toString(8)
}
