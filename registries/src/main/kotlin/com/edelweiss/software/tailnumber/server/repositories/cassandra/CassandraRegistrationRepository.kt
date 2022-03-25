package com.edelweiss.software.tailnumber.server.repositories.cassandra

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.querybuilder.QueryBuilder
import com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal
import com.edelweiss.software.tailnumber.server.common.Config
import com.edelweiss.software.tailnumber.server.core.exceptions.RegistrationNotFoundException
import com.edelweiss.software.tailnumber.server.core.registration.Registration
import com.edelweiss.software.tailnumber.server.core.registration.RegistrationId
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

    override fun findByRegistrationId(registrationId: RegistrationId): Registration =
        getSession().let { session ->
            val select = QueryBuilder.selectFrom("registrations")
                .column("record")
                .whereColumn("id").isEqualTo(literal(registrationId.id))
                .build()
            val resultSet = session.execute(select)
            resultSet.one()?.let { row ->
                json.decodeFromString(row.getString("record")!!)
            } ?: throw RegistrationNotFoundException(registrationId)
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