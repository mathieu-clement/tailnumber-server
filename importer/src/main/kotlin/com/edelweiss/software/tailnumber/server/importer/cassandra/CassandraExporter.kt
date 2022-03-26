package com.edelweiss.software.tailnumber.server.importer.cassandra

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.BatchStatement
import com.datastax.oss.driver.api.core.cql.DefaultBatchType
import com.datastax.oss.driver.api.core.cql.PreparedStatement
import com.edelweiss.software.tailnumber.server.common.Config
import com.edelweiss.software.tailnumber.server.core.registration.Registration
import com.edelweiss.software.tailnumber.server.core.serializers.CoreSerialization
import com.edelweiss.software.tailnumber.server.importer.RegistrationImporter
import com.edelweiss.software.tailnumber.server.importer.faa.FaaRegistrationImporter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress

class CassandraExporter(val importer: RegistrationImporter) {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val json = Json {
        prettyPrint = false
        serializersModule = CoreSerialization.serializersModule
    }

    private val cassandraKeyspace = Config.getString("cassandra.keyspace")
    private val cassandraHost = Config.getString("cassandra.contact-point.host")
    private val cassandraPort = Config.getInt("cassandra.contact-point.port")
    private val cassandraDataCenter = Config.getString("cassandra.datacenter")

    fun export() {
        createSession().use { session ->
//                https://docs.datastax.com/en/developer/java-driver/4.13/manual/core/statements/batch/

            // TODO We could do a live update by deleting records that have disappeared and update those that still exist

            val preparedInsert: PreparedStatement = session.prepare(
                "INSERT INTO registrations (id, country, record) " +
                        "VALUES (:id, :country, :record)"
            )

            var counter = 0

            importer.import().chunked(1).forEach { chunk: List<Registration> ->
                val batchBuilder = BatchStatement.builder(DefaultBatchType.LOGGED)
                chunk.forEach { registration ->
                    batchBuilder.addStatement(
                        preparedInsert.bind(
                            registration.registrationId.id,
                            registration.registrationId.country.name,
                            json.encodeToString(registration)
                        )
                    )
                }
                val batch = batchBuilder.build()
                session.execute(batch)

                counter += 1
                print("\r$counter")
            }


            /*
            importer.import().forEach { registration ->
                val insert = insertInto("registrations")
                    .value("id", literal(registration.registrationId.id))
                    .value("country", literal(registration.registrationId.country.name))
                    .value("record", literal(json.encodeToString(registration)))
                    .build()
                session.execute(insert)
                counter++
                print("\r$counter")
            }
             */
        }
    }

    private fun createSession() = CqlSession.builder()
        .withKeyspace(cassandraKeyspace)
        .addContactPoint(InetSocketAddress(cassandraHost, cassandraPort))
        .withLocalDatacenter(cassandraDataCenter)
        .build()
}

fun main(args: Array<String>) {
    val basePath = args[0]
    val importer = FaaRegistrationImporter(basePath)
    val exporter = CassandraExporter(importer)
    exporter.export()
}