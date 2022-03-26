package com.edelweiss.software.tailnumber.server.api.plugins

import com.edelweiss.software.tailnumber.server.api.services.RegistrationService
import com.edelweiss.software.tailnumber.server.repositories.RegistrationRepository
import com.edelweiss.software.tailnumber.server.repositories.cassandra.CassandraRegistrationRepository
import com.edelweiss.software.tailnumber.server.search.elastic.RegistrationSearchService
import io.ktor.application.*
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.koin.ktor.ext.Koin
import org.koin.logger.slf4jLogger

fun Application.configureInjection() {
    install(Koin) {
        slf4jLogger(level = Level.ERROR) // TODO remove workaround https://youtrack.jetbrains.com/issue/KTOR-3575
        modules(module {
            single<RegistrationRepository> { CassandraRegistrationRepository() }
            single { RegistrationSearchService() }
            single { RegistrationService() }
        })
    }
}