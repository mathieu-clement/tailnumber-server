package com.edelweiss.software.tailnumber.server.importer.elastic

import com.edelweiss.software.tailnumber.server.core.Country
import com.edelweiss.software.tailnumber.server.core.registration.Registration
import com.edelweiss.software.tailnumber.server.importer.RegistrationImporter
import com.edelweiss.software.tailnumber.server.importer.ch.ChRegistrationSummaryImporter
import com.edelweiss.software.tailnumber.server.importer.faa.FaaRegistrationImporter
import com.edelweiss.software.tailnumber.server.importer.zipcodes.ZipCodeRepository
import com.edelweiss.software.tailnumber.server.repositories.RegistrationRepository
import com.edelweiss.software.tailnumber.server.repositories.cassandra.CassandraRegistrationRepository
import com.edelweiss.software.tailnumber.server.search.elastic.ElasticRegistrationSearchService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis

class ElasticExporter : KoinComponent {

    private val faaImporter by inject<RegistrationImporter>(named(Country.US))
    private val chSummaryImporter by inject<RegistrationImporter>(named(Country.CH))

    private val elasticRegistrationService by inject<ElasticRegistrationSearchService>()

    private val logger = LoggerFactory.getLogger(javaClass)

    fun export(runInParallel: Boolean = false, countries: Set<Country> = Country.values().toSet()) {
        elasticRegistrationService.checkConnection()

        val counter = AtomicInteger(0)
        val startTime = System.currentTimeMillis()

        val registrations: MutableList<Registration> = mutableListOf()
        if (Country.CH in countries) {
            logger.info("Export CH data")
            registrations += chSummaryImporter.import()
        }
        if (Country.US in countries) {
            logger.info("Exporting FAA data")
            registrations += faaImporter.import()
        }

        val numRegistrations = registrations.size
        var errorCount = 0

        if (runInParallel) {
            updateInParallel(registrations, startTime, counter, numRegistrations)
        } else {
            val timeMs = measureTimeMillis {
                registrations
                    .sorted()
                    .forEach { registration ->
                        try {
                            elasticRegistrationService.insertOrUpdate(registration)
                            errorCount = 0
                        } catch (t: Throwable) {
                            errorCount++
                            if (errorCount == 10) {
                                logger.error("10 errors in a row. Stopping.")
                                throw t
                            } else {
                                logger.error("Error inserting record ${registration.registrationId.id}", t)
                                logger.info(registration.toString())
                            }
                        }
                        printProgress(startTime, counter.incrementAndGet(), numRegistrations)
                    }
            }
            println("Export finished in ${TimeUnit.MILLISECONDS.toMinutes(timeMs)} min")
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
                .chunked(registrations.size / 4)
                .map { sublist ->
                    async {
                        sublist.forEach { registration ->
                            elasticRegistrationService.insertOrUpdate(registration)
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
}

fun main(args: Array<String>) {
    require(args.size > 1) { "Usage: ElasticExporter US,CH /home/XYZ/tailnumber-data" }
    val countries = args[0].split(",").map { Country.valueOf(it) }.toSet()
    val faaBasePath = args[1] + "/faa/ReleasableAircraft"
    val chSummaryPath = args[1] + "/ch/pubs.html"

    startKoin {
        modules(module {
            single { ZipCodeRepository() }
            single<RegistrationImporter>(named(Country.US)) { FaaRegistrationImporter(faaBasePath) }
            single<RegistrationImporter>(named(Country.CH)) { ChRegistrationSummaryImporter(chSummaryPath, true) }
            single<RegistrationRepository> { CassandraRegistrationRepository() }
            single { ElasticRegistrationSearchService() }
            single { ElasticExporter() }
        })

        koin.get<ElasticExporter>().export(countries = countries)
    }
}