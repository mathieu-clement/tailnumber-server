package com.edelweiss.software.tailnumber.server.search.elastic

import com.edelweiss.software.tailnumber.server.common.Config
import com.edelweiss.software.tailnumber.server.core.Country
import com.edelweiss.software.tailnumber.server.core.registration.*
import com.edelweiss.software.tailnumber.server.core.serializers.CoreSerialization
import com.edelweiss.software.tailnumber.server.search.elastic.dto.request.UpsertDoc
import com.edelweiss.software.tailnumber.server.search.elastic.dto.request.search.*
import com.edelweiss.software.tailnumber.server.search.elastic.dto.response.SearchResponse
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Request
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

class ElasticRegistrationSearchService : KoinComponent {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        serializersModule = CoreSerialization.serializersModule
    }

    private val elasticIndex = Config.getString("elastic.index.registrations")
    private val elasticHost = Config.getString("elastic.host")
    private val elasticPort = Config.getInt("elastic.port")
    private val elasticUser = Config.getString("elastic.user")
    private val elasticPassword = Config.getString("elastic.password")
    private val baseUrl = "https://$elasticHost:$elasticPort/$elasticIndex"
    private val requestTimeoutMs = Config.getInt("elastic.timeoutMs")

    private val partialRegistrationFields = setOf(
        "registrationId.id",
        "registrationId.country",
        "owner",
        "operator",
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

    private val registrantNameOrAddressSearchFields = listOf(
        "registrant.name",
        "registrant.address.street1",
        "registrant.address.street2",
        "registrant.address.city",
        "registrant.address.zipCode5",
        "owner", "operator", "coOwners"
    )

    private val exactRegistrantNameOrAddressSearchFields = listOf(
        "registrant.name.raw",
        "owner.raw", "operator.raw"
    )

    init {
        configureKeystore()
    }

    /**
     * Returns true if there is a record matching the registration
     */
    fun exists(tailNumber: RegistrationId): Boolean {
        val (request, response, result) = Fuel.get("$baseUrl/_doc/${tailNumber.id}")
            .timeout(requestTimeoutMs)
            .timeoutRead(requestTimeoutMs)
            .response()
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

    /**
     * Find up to 20 registrations matching the given prefix (should _not_ include wildcard / "*")
     */
    fun autocompleteRegistration(prefix: String): List<PartialRegistration> {
        val searchDoc = SearchDoc(
            query = QueryDoc(prefix = PrefixQuery(registrationIdId = addDash(prefix))),
            fields = setOf("registrationId.id", "registrationId.country", "aircraftReference.model"),
            size = 20
        )
        val searchDocJson = json.encodeToString(searchDoc)
        val (request, response, result) = Fuel.post("$baseUrl/_search")
            .jsonBody(searchDocJson)
            .configure()
            .responseObject<SearchResponse>(json = json)

        val searchResult = result.get()
        return searchResult.hits.hits.mapNotNull { hit ->
            hit.fields?.let { fields ->
                PartialRegistration(
                    registrationId = RegistrationId(
                        fields.registrationIdId,
                        Country.valueOf(fields.registrationIdCountry)
                    ),
                    model = fields.aircraftReferenceModel
                )
            }
        }
    }


    private fun addDash(prefix: String) = when {
        prefix.length >= 3 -> when {
            "-" in prefix -> prefix
            else -> RegistrationId.sanitize(prefix)
        }
        else -> throw IllegalArgumentException("Requires at least 3 characters in prefix")
    }


    fun findRegistrants(name: String): List<String> {
        val searchDoc = SearchDoc(
            query = QueryDoc(
                BooleanQuery(
                    should = setOf(
                        MustQuery(match = MatchQuery(registrantName = name))
                    )
                )
            ),
            fields = setOf("registrant.name", "owner", "operator", "coOwners"),
            size = 10
        )

        val searchDocJson = json.encodeToString(searchDoc)
        val (request, response, result) = Fuel.post("$baseUrl/_search")
            .jsonBody(searchDocJson)
            .configure()
            .responseObject<SearchResponse>(json = json)

        val searchResponse = result.get()
        return searchResponse.hits.hits.mapNotNull { hit ->
            hit.fields?.registrantName
        }
    }


    fun findByRegistrantNameOrAddress(
        names: Set<String>,
        country: Country?,
        exact: Boolean
    ): List<PartialRegistration> {
        // TODO https://kb.objectrocket.com/elasticsearch/how-to-get-unique-values-for-a-field-in-elasticsearch
        val searchDoc = SearchDoc(
            query = QueryDoc(
                BooleanQuery(
                    should = names.map {
                        MustQuery(
                            queryString = QueryString(
                                it,
                                if (exact) exactRegistrantNameOrAddressSearchFields else registrantNameOrAddressSearchFields,
                                "or"
                            )
                        )
                    }.toSet(),
                    must = country?.let { setOf(MustQuery(MatchQuery(registrationCountry = it))) },
                )
            ),
            fields = partialRegistrationFields,
            size = 50
        )
        val searchDocJson = json.encodeToString(searchDoc)
        val (request, response, result) = Fuel.post("$baseUrl/_search")
            .jsonBody(searchDocJson)
            .configure()
            .responseObject<SearchResponse>(json = json)

        val searchResult = result.get()
        return searchResult.hits.hits.mapNotNull { hit ->
            hit.fields?.let { fields ->
                PartialRegistration(
                    RegistrationId(fields.registrationIdId, Country.valueOf(fields.registrationIdCountry)),
                    fields.aircraftReferenceManufacturer,
                    fields.aircraftReferenceModel,
                    fields.aircraftReferenceManufactureYear,
                    registrant = fields.registrantName?.let { registrantName ->
                        StructuredRegistrant(
                            registrantName,
                            Address(
                                fields.registrantAddressStreet1,
                                fields.registrantAddressStreet2,
                                fields.registrantAddressCity,
                                fields.registrantAddressState,
                                fields.registrantAddressZipCode,
                                fields.registrationIdCountry
                            )
                        )
                    },
                    owner = fields.owner?.let { owner ->
                        UnstructuredRegistrant(owner)
                    },
                    operator = fields.operator?.let { operator ->
                        UnstructuredRegistrant(operator)
                    }
                )
            }
        }
    }

    fun checkConnection() {
        val path = "$baseUrl/_count"
        logger.debug("Attempting connection to \"$path\"")
        val (_, response, result) = Fuel.get(path)
            .configure()
            .responseString()
        val responseString = result.get()
        check(response.statusCode in 200 until 300) { "Status code was ${response.statusCode}. Maybe the user/password or CA cert is wrong?. Response: $responseString" }
        logger.debug("Elastic Search Connection OK")
        logger.debug("_count response: $responseString")
    }

    fun insertOrUpdate(registration: Registration) {
        val upsertJson = json.encodeToString(UpsertDoc(registration))
        logger.debug("Inserting document $upsertJson")
        val path = "$baseUrl/_update/${registration.registrationId.id}"
        logger.debug("Path: $path")
        val (_, response, result) = Fuel.post(path)
            .jsonBody(upsertJson)
            .configure()
            .responseString()
        check(response.statusCode in 200 until 300) { "Status code was ${response.statusCode}. Maybe the user/password or CA cert is wrong?. Response: ${result.get()}" }
    }

    private fun configureKeystore() {
        if (!Config.getBoolean("elastic.use-keystore")) return

        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        val elasticKeystorePassword = Config.getString("elastic.keystore.password")
        val password = elasticKeystorePassword.toCharArray()
        val pathname = "${System.getProperty("user.home")}/apps/elasticsearch/truststore.jks"
        logger.info("Loading keystore from $pathname")
        logger.info("Will be logging in as user $elasticUser")
        keyStore.load(File(pathname).inputStream(), password)
        FuelManager.instance.keystore = keyStore
    }

    private fun Request.configure() = apply {
        timeout(requestTimeoutMs)
        timeoutRead(requestTimeoutMs)
        header("Accept", "application/json")
        authentication().basic(elasticUser, elasticPassword)
    }
}

fun main() {
    startKoin {
        modules(module {
            single { ElasticRegistrationSearchService() }
        })

        val service = koin.get<ElasticRegistrationSearchService>()
        val registrations = service.findByRegistrantNameOrAddress(setOf("BROWNE THOMAS JUAN"), Country.US, false)
        println(registrations)
//        val registrants = service.findRegistrants("BROWNE THOMAS JUAN")
//        println(registrants)
    }
}