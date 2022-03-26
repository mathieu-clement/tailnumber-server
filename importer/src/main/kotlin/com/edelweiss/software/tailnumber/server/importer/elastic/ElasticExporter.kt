package com.edelweiss.software.tailnumber.server.importer.elastic

import com.edelweiss.software.tailnumber.server.common.Config
import com.edelweiss.software.tailnumber.server.core.serializers.CoreSerialization
import com.edelweiss.software.tailnumber.server.importer.RegistrationImporter
import com.edelweiss.software.tailnumber.server.importer.faa.FaaRegistrationImporter
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.extensions.jsonBody
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.io.File
import java.security.KeyStore

class ElasticExporter(val importer: RegistrationImporter) {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val json = Json {
        prettyPrint = false
        serializersModule = CoreSerialization.serializersModule
    }

    private val elasticIndex = "registrations"
    private val elasticHost = Config.getString("elastic.host")
    private val elasticPort = Config.getInt("elastic.port")

    fun export() {
        configureKeystore()

        var counter = 0
        importer.import().forEach { registration ->
            Fuel.post("https://$elasticHost:$elasticPort/$elasticIndex/_doc")
                .jsonBody(json.encodeToString(registration))
                .authentication()
                .basic("elastic", "J8QyF0o*2DBWbKuxJ+8l")
                .response()
            counter++
            print("\r$counter")
        }
    }

    fun configureKeystore() {
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        val password = "trustmeimanengineer".toCharArray()
        keyStore.load(
            File("/Users/mathieuclement/dev/elastic/truststore.jks").inputStream(),
            password
        )
        FuelManager.instance.keystore = keyStore
    }
}

fun main(args: Array<String>) {
    val basePath = args[0]
    val importer = FaaRegistrationImporter(basePath)
//    val exporter = CassandraExporter(importer)
//    exporter.export()
    val exporter = ElasticExporter(importer)
    exporter.export()
}