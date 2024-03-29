package com.edelweiss.software.tailnumber.server.importer.cassandra

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.BatchStatement
import com.datastax.oss.driver.api.core.cql.DefaultBatchType
import com.datastax.oss.driver.api.core.cql.PreparedStatement
import com.datastax.oss.driver.api.querybuilder.QueryBuilder
import com.edelweiss.software.tailnumber.server.common.Config
import com.edelweiss.software.tailnumber.server.core.Country
import com.edelweiss.software.tailnumber.server.core.registration.Registration
import com.edelweiss.software.tailnumber.server.core.registration.RegistrationId
import com.edelweiss.software.tailnumber.server.core.serializers.CoreSerialization
import com.edelweiss.software.tailnumber.server.importer.RegistrationImporter
import com.edelweiss.software.tailnumber.server.importer.ch.ChFullRegistrationImporter
import com.edelweiss.software.tailnumber.server.importer.faa.FaaRegistrationImporter
import com.edelweiss.software.tailnumber.server.importer.zipcodes.ZipCodeRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis

class CassandraExporter : KoinComponent {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val faaImporter by inject<RegistrationImporter>(named(Country.US))
    private val chSummaryImporter by inject<RegistrationImporter>(named(Country.CH))

    private val json = Json {
        prettyPrint = false
        serializersModule = CoreSerialization.serializersModule
    }

    private val cassandraKeyspace = Config.getString("cassandra.keyspace")
    private val cassandraHost = Config.getString("cassandra.contact-point.host")
    private val cassandraPort = Config.getInt("cassandra.contact-point.port")
    private val cassandraDataCenter = Config.getString("cassandra.datacenter")

    fun export(countries: Set<Country> = Country.values().toSet()) {
        createSession().use { session ->
//                https://docs.datastax.com/en/developer/java-driver/4.13/manual/core/statements/batch/

            // TODO We could do a live update by deleting records that have disappeared and update those that still exist

            val preparedInsert: PreparedStatement = session.prepare(
                "UPDATE registrations SET country = :country, lastUpdate= :lastUpdate, record = :record " +
                        "WHERE id = :id"
            )

            val counter = AtomicInteger(0)
            val startTimeNanos = System.nanoTime()

            val registrations: MutableList<Registration> = mutableListOf()
            if (Country.CH in countries) {
                logger.info("Exporting CH data")
                registrations += chSummaryImporter.import()
            }
            if (Country.US in countries) {
                logger.info("Exporting FAA data")
                registrations += faaImporter.import()
            }

            val existingRegistrationIds : Set<RegistrationId> = existingRegistrationIds(session)
            val newRegistrationIds : Set<RegistrationId> = registrations.map { it.registrationId }.toSet()
            val deletedRegistrationIds = existingRegistrationIds - newRegistrationIds.intersect(existingRegistrationIds)
            logger.info("Deleted registrations: ${deletedRegistrationIds.map { it.id }}")
            if (deletedRegistrationIds.isNotEmpty()) delete(deletedRegistrationIds, session)

            val numRegistrations = registrations.size // - offset
            val lastUpdate = LocalDate.now()

            val timeMs = measureTimeMillis {
                registrations.chunked(1).forEach { chunk: List<Registration> ->
                    val batchBuilder = BatchStatement.builder(DefaultBatchType.LOGGED)
                    chunk.forEach { registration ->
                        batchBuilder.addStatement(
                            preparedInsert.bind(
                                registration.registrationId.country.name,
                                lastUpdate,
                                json.encodeToString(registration),
                                registration.registrationId.id,
                            )
                        )
                    }
                    val batch = batchBuilder.build()
                    session.execute(batch)

                    printProgress(startTimeNanos, counter.incrementAndGet(), numRegistrations)
                }
            }
            println("Export finished in ${TimeUnit.MILLISECONDS.toMinutes(timeMs)} min")
        }
    }

    private fun delete(ids: Set<RegistrationId>, session: CqlSession) {
        session.execute(
            QueryBuilder.deleteFrom("registrations")
                .whereColumn("id").`in`(ids.map { QueryBuilder.literal(it.id) })
                .build()
        )
    }

    private fun existingRegistrationIds(session: CqlSession): Set<RegistrationId> {
        val resultSet = session.execute(
            QueryBuilder.selectFrom("registrations")
                .column("id")
                .column("country")
                .build())
        return resultSet.map {
            row -> RegistrationId(row.getString("id")!!, Country.valueOf(row.getString("country")!!))
        }.toSet()
    }

    private fun printProgress(startTimeNanos: Long, counter: Int, numRegistrations: Int) {
        if (counter % 100 != 0) return
        val currentTimeNanos = System.nanoTime()
        val averageMillisPerUpsert = (currentTimeNanos - startTimeNanos) / 1e6 / counter
        val remainingUpserts = numRegistrations - counter
        val remainingTimeMillis = remainingUpserts * averageMillisPerUpsert
        val remainingMinutes = TimeUnit.MILLISECONDS.toMinutes(remainingTimeMillis.toLong())
        val remainingSeconds =
            TimeUnit.MILLISECONDS.toSeconds(remainingTimeMillis.toLong()) - TimeUnit.MINUTES.toSeconds(remainingMinutes)
        print("\r$counter (${(100 * counter / numRegistrations)} %), $remainingMinutes min $remainingSeconds sec remaining")
    }

    private fun createSession() = CqlSession.builder()
        .withKeyspace(cassandraKeyspace)
        .addContactPoint(InetSocketAddress(cassandraHost, cassandraPort))
        .withLocalDatacenter(cassandraDataCenter)
        .build()
}

fun main(args: Array<String>) {
    require(args.size > 1) { "Usage: CassandraExporter US,CH /home/XYZ/tailnumber-data" }
    val countries = args[0].split(",").map { Country.valueOf(it) }.toSet()
    val faaDataBasePath = args[1] + "/faa/ReleasableAircraft"
//    val chSummaryPath = args[1] + "/ch/pubs.html"
    val jsonTarGzPath = args[1] + "/ch/json.tar.gz"

    startKoin {
        modules(module {
            single { ZipCodeRepository() }
            single<RegistrationImporter>(named(Country.US)) { FaaRegistrationImporter(faaDataBasePath) }
//            single<RegistrationImporter>(named(Country.CH)) { ChRegistrationSummaryImporter(chSummaryPath, true) }
            single<RegistrationImporter>(named(Country.CH)) { ChFullRegistrationImporter(jsonTarGzPath) }
            single { CassandraExporter() }
        })

        koin.get<CassandraExporter>().export(countries)
    }
}