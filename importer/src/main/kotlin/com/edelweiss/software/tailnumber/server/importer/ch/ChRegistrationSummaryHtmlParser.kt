package com.edelweiss.software.tailnumber.server.importer.ch

import com.edelweiss.software.tailnumber.server.core.registration.Registration

class ChRegistrationSummaryHtmlParser {
    fun import(): List<Registration> {

        return emptyList()
    }

    private fun inputStream() = Thread.currentThread().contextClassLoader.getResourceAsStream(
        "pubs.html"
    )
}

fun main() {
    ChRegistrationSummaryHtmlParser().import()
}