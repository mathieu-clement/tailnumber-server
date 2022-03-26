package com.edelweiss.software.tailnumber.server.search.elastic

import com.edelweiss.software.tailnumber.server.common.Config
import com.edelweiss.software.tailnumber.server.core.Address
import com.edelweiss.software.tailnumber.server.core.Country
import com.edelweiss.software.tailnumber.server.core.registration.Registrant
import com.edelweiss.software.tailnumber.server.core.registration.Registration
import com.edelweiss.software.tailnumber.server.core.registration.RegistrationId
import com.edelweiss.software.tailnumber.server.core.serializers.CoreSerialization
import com.edelweiss.software.tailnumber.server.search.elastic.dto.request.UpsertDoc
import com.edelweiss.software.tailnumber.server.search.elastic.dto.request.search.*
import com.edelweiss.software.tailnumber.server.search.elastic.dto.response.PartialRegistration
import com.edelweiss.software.tailnumber.server.search.elastic.dto.response.SearchResponse
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.serialization.responseObject
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.slf4j.LoggerFactory
import java.io.File
import java.security.KeyStore

class ElasticRegistrationService : KoinComponent {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        serializersModule = CoreSerialization.serializersModule
    }

    private val elasticIndex = "registrations"
    private val elasticHost = Config.getString("elastic.host")
    private val elasticPort = Config.getInt("elastic.port")
    private val elasticUser = Config.getString("elastic.user")
    private val elasticPassword = Config.getString("elastic.password")
    private val baseUrl = "https://$elasticHost:$elasticPort/$elasticIndex"

    private val partialRegistrationFields = setOf(
        "registrationId.id",
        "registrationId.country",
        "registrant.name",
        "registrant.address.street1",
        "registrant.address.street2",
        "registrant.address.city",
        "registration.address.state",
        "registrant.address.zipCode",
        "registrant.address.country",
        "aircraftReference.manufacturer",
        "aircraftReference.model",
        "aircraftReference.manufactureYear"
    )

    init {
        configureKeystore()
    }

    /**
     * Returns true if there is a record matching the registration
     */
    fun exists(tailNumber: RegistrationId) : Boolean {
        val (request, response, result) = Fuel.get("$baseUrl/_doc/${tailNumber.id}").response()
        return response.statusCode == 200
    }

    /*
    partial tailnumber match:

    {
        "query_string": {
            "query": "*",
            "fields": ["registrationId.id"]
        }
    }
     */

    fun findRegistrants(name: String) : List<String> {
        val searchDoc = SearchDoc(
            query = QueryDoc(BooleanQuery(setOf(
                MustQuery(match = MatchQuery(registrantName = name))))),
            fields = setOf("registrant.name"),
            size = 10
        )

        val searchDocJson = json.encodeToString(searchDoc)
        val (request, response, result) = Fuel.post("$baseUrl/_search")
            .jsonBody(searchDocJson)
            .authentication()
            .basic(elasticUser, elasticPassword)
            .responseObject<SearchResponse>(json = json)

        val searchResponse = result.get()
        return searchResponse.hits.hits.mapNotNull { hit ->
            hit.fields?.registrantName
        }
    }



    fun findByRegistrantNames(names: Set<String>) : List<PartialRegistration> {
        // TODO https://kb.objectrocket.com/elasticsearch/how-to-get-unique-values-for-a-field-in-elasticsearch
        val searchDoc = SearchDoc(
            query = QueryDoc(BooleanQuery(should = names.map {
                    MustQuery(queryString = QueryString(it, listOf("registrant.name")))
                }.toSet())),
                fields = partialRegistrationFields)
        val searchDocJson = json.encodeToString(searchDoc)
        val (request, response, result) = Fuel.post("$baseUrl/_search")
            .jsonBody(searchDocJson)
            .authentication()
            .basic(elasticUser, elasticPassword)
            .responseObject<SearchResponse>(json = json)

        val searchResult = result.get()
        return searchResult.hits.hits.mapNotNull { hit ->
            hit.fields?.let { fields ->
                PartialRegistration(
                    RegistrationId(fields.registrationIdId, Country.valueOf(fields.registrationIdCountry)),
                    fields.aircraftReferenceManufacturer, fields.aircraftReferenceModel, fields.aircraftReferenceManufactureYear,
                    Registrant(
                        fields.registrantName,
                        Address(
                            fields.registrantAddressStreet1,
                            fields.registrantAddressStreet2,
                            fields.registrantAddressCity,
                            fields.registrantAddressState,
                            fields.registrantAddressZipCode,
                            fields.registrationIdCountry
                        )
                    )
                )
            }
        }
    }

    fun insertOrUpdate(registration: Registration) {
        val upsertJson = json.encodeToString(UpsertDoc(registration))
        val (_, response, _) = Fuel.post("$baseUrl/_update/${registration.registrationId.id}")
            .jsonBody(upsertJson)
            .authentication()
            .basic(elasticUser, elasticPassword)
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

fun main() {
    startKoin {
        modules(module {
            single { ElasticRegistrationService() }
        })

        val service = koin.get<ElasticRegistrationService>()
        val registrations = service.findByRegistrantNames(setOf("BROWNE THOMAS JUAN"))
        println(registrations)
//        val registrants = service.findRegistrants("BROWNE THOMAS JUAN")
//        println(registrants)
    }
}