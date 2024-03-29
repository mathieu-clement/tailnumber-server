package com.edelweiss.software.tailnumber.server.api.plugins

import io.ktor.application.*
import io.ktor.features.*

fun Application.configureHTTP() {
    install(Compression) {
        gzip {
            priority = 1.0
        }
        deflate {
            priority = 10.0
            minimumSize(1024)
        }
    }

    // CachingHeaders
}