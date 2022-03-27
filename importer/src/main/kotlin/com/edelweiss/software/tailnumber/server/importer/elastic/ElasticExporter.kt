package com.edelweiss.software.tailnumber.server.importer.elastic

import com.edelweiss.software.tailnumber.server.core.registration.Registration
import com.edelweiss.software.tailnumber.server.importer.RegistrationImporter
import com.edelweiss.software.tailnumber.server.importer.faa.FaaRegistrationImporter
import com.edelweiss.software.tailnumber.server.importer.zipcodes.ZipCodeRepository
import com.edelweiss.software.tailnumber.server.repositories.RegistrationRepository
import com.edelweiss.software.tailnumber.server.repositories.cassandra.CassandraRegistrationRepository
import com.edelweiss.software.tailnumber.server.search.elastic.RegistrationSearchService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class ElasticExporter(private val offset: Int = 0) : KoinComponent {

    private val importer by inject<RegistrationImporter>()

    private val elasticRegistrationService by inject<RegistrationSearchService>()

    private val logger = LoggerFactory.getLogger(javaClass)

    fun export(runInParallel: Boolean = false) {
        val counter = AtomicInteger(0)
        val startTime = System.currentTimeMillis()

        val registrations = importer.import()
        val numRegistrations = registrations.size - offset

        if (offset != 0) logger.warn("#### Starting from offset $offset ####")

        if (runInParallel) {
            updateInParallel(registrations, startTime, counter, numRegistrations)
        } else {
            registrations
                .sorted()
                .subList(offset, registrations.size)
                .forEach { registration ->
                    elasticRegistrationService.insertOrUpdate(registration)
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
    val basePath = args[0]
    val offset = if (args.size > 1) args[1].toInt() else 0

    startKoin {
        modules(module {
            single { ZipCodeRepository() }
            single { FaaRegistrationImporter(basePath) }
            single<RegistrationRepository> { CassandraRegistrationRepository() }
            single { RegistrationSearchService() }
            single { ElasticExporter(offset) }
        })

        koin.get<ElasticExporter>().export()
    }
}