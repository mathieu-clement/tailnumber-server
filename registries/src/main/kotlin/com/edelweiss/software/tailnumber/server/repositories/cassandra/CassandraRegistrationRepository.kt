package com.edelweiss.software.tailnumber.server.repositories.cassandra

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.querybuilder.QueryBuilder
import com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal
import com.edelweiss.software.tailnumber.server.common.Config
import com.edelweiss.software.tailnumber.server.core.exceptions.RegistrationsNotFoundException
import com.edelweiss.software.tailnumber.server.core.registration.Registration
import com.edelweiss.software.tailnumber.server.core.registration.RegistrationId
import com.edelweiss.software.tailnumber.server.core.registration.RegistrationResult
import com.edelweiss.software.tailnumber.server.core.serializers.CoreSerialization
import com.edelweiss.software.tailnumber.server.repositories.RegistrationRepository
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress

class CassandraRegistrationRepository : RegistrationRepository {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val json = Json {
        serializersModule = CoreSerialization.serializersModule
    }

    private lateinit var _session : CqlSession

    private val cassandraKeyspace = Config.getString("cassandra.keyspace")
    private val cassandraHost = Config.getString("cassandra.contact-point.host")
    private val cassandraPort = Config.getInt("cassandra.contact-point.port")
    private val cassandraDataCenter = Config.getString("cassandra.datacenter")

    override fun findByRegistrationIds(registrationIds: List<RegistrationId>): List<RegistrationResult> =
        getSession().let { session ->
            val select = QueryBuilder.selectFrom("registrations")
                .column("record")
                .column("lastUpdate")
                .whereColumn("id").`in`(registrationIds.map { literal(it.id) })
                .build()
            val resultSet = session.execute(select)
            return resultSet.map { row ->
                RegistrationResult(
                    row.getLocalDate("lastUpdate"),
                    json.decodeFromString<Registration>(row.getString("record")!!)
                )
            }.toList()
                .ifEmpty { throw RegistrationsNotFoundException(registrationIds) }
        }

    private fun getSession() : CqlSession {
        if (!this::_session.isInitialized) {
            _session = createSession()
        }
        return _session
    }

    private fun createSession() = CqlSession.builder()
        .withKeyspace(cassandraKeyspace)
        .addContactPoint(InetSocketAddress(cassandraHost, cassandraPort))
        .withLocalDatacenter(cassandraDataCenter)
        .build()
}