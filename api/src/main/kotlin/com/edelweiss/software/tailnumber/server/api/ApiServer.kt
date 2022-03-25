package com.edelweiss.software.tailnumber.server.api

import com.edelweiss.software.tailnumber.server.api.plugins.configureHTTP
import com.edelweiss.software.tailnumber.server.api.plugins.configureInjection
import com.edelweiss.software.tailnumber.server.api.plugins.configureRouting
import com.edelweiss.software.tailnumber.server.api.plugins.configureSerialization
import com.edelweiss.software.tailnumber.server.common.Config
import io.ktor.application.*
import io.ktor.network.tls.certificates.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.LoggerFactory
import java.io.File

fun Application.module() {
    configureInjection()
    configureHTTP()
    configureSerialization()
    configureRouting()
}

fun main() {
    val keyStoreFile = File("build/keystore.jks")
    val keystore = generateCertificate(
        file = keyStoreFile,
        keyAlias = "sampleAlias",
        keyPassword = "foobar",
        jksPassword = "foobar"
    )

    val environment = applicationEngineEnvironment {
        log = LoggerFactory.getLogger("ktor.application")
        connector {
            port = Config.getInt("api.server.http.port")
        }
        sslConnector(
            keyStore = keystore,
            keyAlias = "sampleAlias",
            keyStorePassword = { "foobar".toCharArray() },
            privateKeyPassword = { "foobar".toCharArray() }) {
            port = Config.getInt("api.server.https.port")
            keyStorePath = keyStoreFile
        }
        module(Application::module)
    }

    embeddedServer(Netty, environment).start(true)
}