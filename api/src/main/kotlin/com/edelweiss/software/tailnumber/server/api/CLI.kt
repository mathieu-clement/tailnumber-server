package com.edelweiss.software.tailnumber.server.api

import com.edelweiss.software.tailnumber.server.repositories.RegistrationRepository
import com.edelweiss.software.tailnumber.server.repositories.cassandra.CassandraRegistrationRepository
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun main() {
    startKoin {
        modules(module {
            single<RegistrationRepository> { CassandraRegistrationRepository() }
        })

        val repo = koin.get<RegistrationRepository>()
        val registration = repo.findByRegistrationId("N9076H")
        println(registration)
    }
}