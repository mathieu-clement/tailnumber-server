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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.io.File
import java.security.KeyStore
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class ElasticExporter(val importer: RegistrationImporter, val offset: Int = 0) {

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

        val counter = AtomicInteger(0)
        val startTime = System.currentTimeMillis()

        val registrations = importer.import()
        val numRegistrations = registrations.size

        if (offset != 0) logger.warn("#### Starting from offset $offset ####")

        val runInParallel = false

        if (runInParallel) {
            updateInParallel(registrations, startTime, counter, numRegistrations)
        } else {
            registrations
                .sorted()
                .subList(offset, registrations.size)
                .forEach { registration ->
                    update(registration)
                    printProgress(startTime, counter.incrementAndGet(), numRegistrations)
                }
        }
    }

    private fun updateInParallel(
        registrations: List<Registration>,
        startTime: Long,
        counter: AtomicInteger,
        numRegistrations: Int
    ) {
        runBlocking {
            val deferreds = registrations
                .sorted()
                .subList(offset, registrations.size)
                .chunked(registrations.size / 4)
                .map { sublist ->
                    async {
                        sublist.forEach { registration ->
                            update(registration)
                            printProgress(startTime, counter.incrementAndGet(), numRegistrations)
                        }
                    }
                }
            deferreds.awaitAll()
        }
    }

    private fun printProgress(startTime: Long, counter: Int, numRegistrations: Int) {
        if (counter % 100 != 0) return
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
    val offset = if (args.size > 1) args[1].toInt() else 0
    val importer = FaaRegistrationImporter(basePath)
//    val exporter = CassandraExporter(importer)
//    exporter.export()
    val exporter = ElasticExporter(importer, offset)
    exporter.export()
}