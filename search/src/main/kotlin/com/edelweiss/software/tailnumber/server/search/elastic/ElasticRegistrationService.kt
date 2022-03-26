package com.edelweiss.software.tailnumber.server.search.elastic

import com.edelweiss.software.tailnumber.server.common.Config
import com.edelweiss.software.tailnumber.server.core.registration.Registration
import com.edelweiss.software.tailnumber.server.core.registration.RegistrationId
import com.edelweiss.software.tailnumber.server.core.serializers.CoreSerialization
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.extensions.jsonBody
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.slf4j.LoggerFactory
import java.io.File
import java.security.KeyStore

class ElasticRegistrationService : KoinComponent {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val json = Json {
        prettyPrint = false
        serializersModule = CoreSerialization.serializersModule
    }

    private val elasticIndex = "registrations"
    private val elasticHost = Config.getString("elastic.host")
    private val elasticPort = Config.getInt("elastic.port")
    private val baseUrl = "https://$elasticHost:$elasticPort/$elasticIndex"

    init {
        configureKeystore()
    }

    /**
     * Returns true if there is a record matching the registration
     */
    fun findByRegistrationId(tailNumber: RegistrationId) : Boolean {
        return false
    }

    fun insertOrUpdate(registration: Registration) {
        val upsertJson = json.encodeToString(UpsertDoc(registration))
        val (_, response, _) = Fuel.post("$baseUrl/_update/${registration.registrationId.id}")
            .jsonBody(upsertJson)
            .authentication()
            .basic("elastic", "J8QyF0o*2DBWbKuxJ+8l")
            .response()
        check(response.statusCode in 200 until 300) { "Status code was ${response.statusCode}: ${String(response.data)}" }
    }

    private fun configureKeystore() {
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        val password = "trustmeimanengineer".toCharArray()
        keyStore.load(
            File("/Users/mathieuclement/dev/elastic/truststore.jks").inputStream(),
            password
        )
        FuelManager.instance.keystore = keyStore
    }
}