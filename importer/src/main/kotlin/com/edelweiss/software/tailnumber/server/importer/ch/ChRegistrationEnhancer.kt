package com.edelweiss.software.tailnumber.server.importer.ch

import com.edelweiss.software.tailnumber.server.core.Country
import com.edelweiss.software.tailnumber.server.core.aircraft.AircraftReference
import com.edelweiss.software.tailnumber.server.core.aircraft.AircraftType
import com.edelweiss.software.tailnumber.server.core.aircraft.Weight
import com.edelweiss.software.tailnumber.server.core.aircraft.WeightUnit
import com.edelweiss.software.tailnumber.server.core.engine.EngineReference
import com.edelweiss.software.tailnumber.server.core.registration.*
import com.edelweiss.software.tailnumber.server.importer.ch.models.ChRegistrationStatus
import com.edelweiss.software.tailnumber.server.importer.ch.models.request.QueryProperties
import com.edelweiss.software.tailnumber.server.importer.ch.models.request.RegistryRequest
import com.edelweiss.software.tailnumber.server.importer.ch.models.response.OwnerOperator
import com.edelweiss.software.tailnumber.server.importer.ch.models.response.RegistryRecord
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.serialization.responseObject
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import org.slf4j.LoggerFactory

class ChRegistrationEnhancer {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun fetchRegistration(registrationId: RegistrationId): Registration {
        val requestBody = json.encodeToString(
            RegistryRequest.serializer(),
            RegistryRequest(QueryProperties(registration = registrationId.id))
        )
        val (request, response, result) = Fuel.post("https://app02.bazl.admin.ch/web/bazl-backend/lfr")
            .jsonBody(requestBody)
            .responseObject<JsonArray>(json = json)

        val records: List<RegistryRecord> = result.get().map {
            json.decodeFromJsonElement(RegistryRecord.serializer(), it)
        }
        check(records.isNotEmpty()) { "No result for ${registrationId.id}" }
        check(records.size == 1) { "More than one result for ${registrationId.id}" }

        val record = records[0]
        return toRegistration(record)
    }

    private fun toRegistration(record: RegistryRecord): Registration {
        val numEngines = record.details?.engines?.mapNotNull {
            it.count
        }?.reduce { a, b -> a + b } ?: 0
        val aircraftCategoryTranslation = record.aircraftCategories
            ?.aircraftCategoryTranslations?.firstOrNull { it.lang == "en" }
            ?.text
        val aircraftType = aircraftCategoryTranslation?.let { toAircraftType(it, numEngines) }

        return Registration(
            registrationId = RegistrationId.fromTailNumber(record.registration),
            recordId = record.lfrId,
            status = toRegistrationStatus(record.status),
            aircraftReference = AircraftReference(
                aircraftType = aircraftType,
                manufacturer = record.manufacturer,
                model = record.aircraftModelType,
                marketingDesignation = record.details?.marketing,
                icaoType = record.icaoCode,
                serialNumber = record.details?.serialNumber,
                typeCertificated = when {
                    aircraftCategoryTranslation == null -> null
                    aircraftCategoryTranslation.contains("Homebuil") -> false
                    else -> true
                },
                engines = numEngines,
                aircraftCategory = null,
                seats = null,
                passengerSeats = record.details?.numCrewPax,
                weightCategory = null,
                maxTakeOffMass = record.details?.mtom?.let { Weight(it, WeightUnit.KILOGRAMS) },
                cruisingSpeed = null,
                manufactureYear = record.details?.yearOfManufacture,
                kitManufacturerName = null,
                kitModelName = null,
                transponderCode = record.details?.aircraftAddresses?.oct?.let { TransponderCode.fromOctal(it) }
            ),
            engineReferences = record.details?.engines?.map {
                EngineReference(
                    count = it.count,
                    engineType = null,
                    manufacturer = it.engineManufacturer,
                    model = it.name
                )
            } ?: emptyList(),
            registrantType = null,
            owner = ownerOperatorToStructuredRegistrant(record, "Main Owner"),
            operator = ownerOperatorToStructuredRegistrant(record, "Main Operator"),
            certificateIssueDate = record.details?.regDate
            // TODO deregDate
            // TODO record.details?.airworthinessCat
            // TODO certificationBasis, numCrew, numCrewPax, ela, eltCode, brs, propellers, noiseLevel, noiseStandard, aircraftLegalBasis, tcds
        )
    }

    private fun ownerOperatorToStructuredRegistrant(record: RegistryRecord, registrantType: String) =
        record.ownerOperators
            ?.firstOrNull { it.holderCategory.categoryNames.en == registrantType }
            ?.let { toStructuredRegistrant(it) }

    private fun toStructuredRegistrant(o: OwnerOperator) = StructuredRegistrant(
        name = o.ownerOperator,
        address = o.address?.let { addr ->
            val streetWithHouseNumber = addr.street?.let { street ->
                if (addr.streetNo != null) "$street ${addr.streetNo}" else street
            }
            Address(
                street1 = addr.extraLine ?: streetWithHouseNumber,
                street2 = if (addr.extraLine != null) streetWithHouseNumber else null,
                poBox = addr.poBoxName?.let { poBoxName -> "$poBoxName ${addr.poBox!!}" },
                city = addr.city,
                zipCode = addr.zipCode,
                country = addr.country
            )
        }
    )

    private fun toAircraftType(text: String, engines: Int): AircraftType? = when (text) {
        "Aeroplane", "Homebuilt Airplane" -> if (engines > 1)
            AircraftType.FIXED_WING_MULTI_ENGINE
        else
            AircraftType.FIXED_WING_SINGLE_ENGINE

        "Balloon (Gas)", "Balloon (Hot-air)" -> AircraftType.BALLOON
        "Powered Glider" -> AircraftType.POWERED_GLIDER
        "Helicopter", "Homebuilt Helicopter" -> AircraftType.HELICOPTER
        "Airship (Hot-air)" -> AircraftType.AIRSHIP
        "Glider", "Homebuild Glider", "Homebuilt Glider" -> AircraftType.GLIDER
        "Homebuilt Gyrocopter" -> AircraftType.GYROPLANE
        "Ultralight Gyrocopter" -> AircraftType.ULTRALIGHT_GYROPLANE
        "Ecolight" -> AircraftType.ECOLIGHT
        "Trike" -> AircraftType.TRIKE
        "Ultralight (3-axis control)" -> AircraftType.ULTRALIGHT_3_AXIS_CONTROL
        else -> {
            logger.warn("Unknown aircraft type: $text")
            null
        }
    }

    private fun toRegistrationStatus(status: ChRegistrationStatus?): RegistrationStatus? = when (status) {
        ChRegistrationStatus.REGISTERED -> RegistrationStatus.REGISTERED
        ChRegistrationStatus.EXPIRED -> RegistrationStatus.EXPIRED
        ChRegistrationStatus.RESERVED -> RegistrationStatus.RESERVED
        ChRegistrationStatus.IN_PROGRESS -> RegistrationStatus.PENDING
        else -> {
            logger.warn("Conversion not available for status: $status")
            null
        }
    }
}

fun main() {
    val enhancer = ChRegistrationEnhancer()
    val reg = enhancer.fetchRegistration(RegistrationId("HB-CQR", Country.CH))
    println(reg)
}