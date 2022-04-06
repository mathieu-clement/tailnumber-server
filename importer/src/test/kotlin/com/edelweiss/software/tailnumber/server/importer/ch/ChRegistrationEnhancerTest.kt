package com.edelweiss.software.tailnumber.server.importer.ch

import com.edelweiss.software.tailnumber.server.core.Country
import com.edelweiss.software.tailnumber.server.core.registration.RegistrationId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ChRegistrationEnhancerTest {

    private val enhancer = ChRegistrationEnhancer()

    @Test
    fun fetchRegistration() {
        val string = readFileAsString("HB-5002.json")
        val registration = enhancer.fetchRegistration(RegistrationId("HB-5002", Country.CH), string)
        assertEquals("SINUS", registration.aircraftReference.model)
    }

    private fun readFileAsString(filename: String): String {
        val stream = Thread.currentThread().contextClassLoader.getResourceAsStream(filename)
        return stream!!.bufferedReader().use {
            it.readText()
        }
    }
}