package com.edelweiss.software.tailnumber.server.importer.elastic

import com.edelweiss.software.tailnumber.server.common.Config
import com.edelweiss.software.tailnumber.server.core.registration.Registration
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
import java.util.concurrent.TimeUnit

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
        val startTime = System.currentTimeMillis()

        val registrations = importer.import()
        val numRegistrations = registrations.size

        registrations.forEach { registration ->
            update(registration)
            printProgress(startTime, ++counter, numRegistrations)
        }
    }

    private fun printProgress(startTime: Long, counter: Int, numRegistrations: Int) {
        val currentTime = System.currentTimeMillis()
        val averageMillisPerUpsert = (currentTime - startTime) / counter
        val remainingUpserts = numRegistrations - counter
        val remainingTimeMillis = remainingUpserts * averageMillisPerUpsert
        val remainingMinutes = TimeUnit.MILLISECONDS.toMinutes(remainingTimeMillis)
        val remainingSeconds =
            TimeUnit.MILLISECONDS.toSeconds(remainingTimeMillis) - TimeUnit.MINUTES.toSeconds(remainingMinutes)
        print("\r$counter (${(100 * counter / numRegistrations)} %), $remainingMinutes min $remainingSeconds sec remaining")
    }

    private fun update(registration: Registration) {
        val upsertJson = json.encodeToString(UpsertDoc(registration))
        val (request, response, result) = Fuel.post("https://$elasticHost:$elasticPort/$elasticIndex/_update/${registration.registrationId.id}")
            .jsonBody(upsertJson)
            .authentication()
            .basic("elastic", "J8QyF0o*2DBWbKuxJ+8l")
            .response()
        check(response.statusCode in 200 until 300) { "Status code was ${response.statusCode}: ${String(response.data)}" }
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