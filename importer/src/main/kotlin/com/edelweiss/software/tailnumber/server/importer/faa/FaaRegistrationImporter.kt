package com.edelweiss.software.tailnumber.server.importer.faa

import com.edelweiss.software.tailnumber.server.core.Country
import com.edelweiss.software.tailnumber.server.core.aircraft.*
import com.edelweiss.software.tailnumber.server.core.airworthiness.Airworthiness
import com.edelweiss.software.tailnumber.server.core.airworthiness.AirworthinessCertificateClass
import com.edelweiss.software.tailnumber.server.core.airworthiness.AirworthinessOperation
import com.edelweiss.software.tailnumber.server.core.engine.*
import com.edelweiss.software.tailnumber.server.core.registration.*
import com.edelweiss.software.tailnumber.server.importer.RegistrationImporter
import com.edelweiss.software.tailnumber.server.importer.faa.acftref.AcftRefImporter
import com.edelweiss.software.tailnumber.server.importer.faa.acftref.AcftRefRecord
import com.edelweiss.software.tailnumber.server.importer.faa.engine.EngineImporter
import com.edelweiss.software.tailnumber.server.importer.faa.engine.EngineRecord
import com.edelweiss.software.tailnumber.server.importer.faa.master.MasterImporter
import com.edelweiss.software.tailnumber.server.importer.faa.master.MasterRecord
import com.edelweiss.software.tailnumber.server.importer.zipcodes.ZipCodeRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FaaRegistrationImporter(
    basePath: String,
    acftRefFilename: String = "ACFTREF.txt",
    engineFilename: String = "ENGINE.txt",
    masterFilename: String = "MASTER.txt"
) : RegistrationImporter, KoinComponent {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val zipCodeRepository by inject<ZipCodeRepository>()

    private val acftRefImporter = AcftRefImporter("$basePath/$acftRefFilename")
    private val engineImporter = EngineImporter("$basePath/$engineFilename")
    private val masterImporter = MasterImporter("$basePath/$masterFilename")

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

    override fun import(): List<Registration> {
        logger.info("Importing ENGINE")
        val engineRecords = engineImporter.import().associateBy { it.code }

        logger.info("Importing ACFTREF")
        val acftRefRecords = acftRefImporter.import().associateBy { it.code }

        logger.info("Importing MASTER")
        val masterRecords = masterImporter.import()

        val numMasterRecords = masterRecords.size
        logger.info("MASTER records: $numMasterRecords")
        var counter = 0

        return masterRecords.map { masterRecord ->
            toRegistration(
                masterRecord,
                acftRefRecords[masterRecord.mfrMdlCode],
                engineRecords[masterRecord.engMfrMdl]
            ).also {
                if (logger.isDebugEnabled) {
                    counter += 1
                    if (counter % 10000 == 0) {
                        logger.debug("$counter / $numMasterRecords (${(counter * 100 / numMasterRecords)} %)")
                    }
                }
            }
        }
    }

    private fun toRegistration(
        masterRecord: MasterRecord,
        acftRefRecord: AcftRefRecord?,
        engineRecord: EngineRecord?
    ): Registration {
        val registration = Registration(
            registrationId = RegistrationId("N" + masterRecord.nNumber, Country.US),
            recordId = masterRecord.uniqueId,
            status = RegistrationStatus.fromFaaCode(masterRecord.statusCode),
            aircraftReference = AircraftReference(
                aircraftType = AircraftType.fromFaaCode(masterRecord.typeAircraft),
                aircraftCategory = acftRefRecord?.let { AircraftCategory.fromFaaCode(it.acCat) },
                manufacturer = acftRefRecord?.mfr,
                model =  acftRefRecord?.model,
                serialNumber = masterRecord.serialNumber,
                typeCertificated = acftRefRecord?.buildCertInd == 0,
                engines = acftRefRecord?.noEng,
                seats = acftRefRecord?.noSeats,
                weightCategory = acftRefRecord?.let { WeightCategory.fromFaaCode(it.acWeight) },
                cruisingSpeed = acftRefRecord?.speed?.let { Speed(it, SpeedUnit.MPH) }.takeIf { it?.value != 0 },
                manufactureYear = masterRecord.yearMfr,
                kitManufacturerName = masterRecord.kitMfr,
                kitModelName = masterRecord.kitModel,
                transponderCode = TransponderCode.fromHex(masterRecord.modeSCodeHex),
            ),
            engineReferences = engineRecord?.let {
                listOf(EngineReference(
                    count = acftRefRecord?.noEng,
                    engineType = it.type?.let { type -> EngineType.fromFaaCode(type) },
                    manufacturer = it.mfr,
                    model = it.model,
                    power = it.horsepower?.let { hp -> Power(hp, PowerUnit.SAE_HP) },
                    thrust = it.thrust?.let { thrust -> Thrust(thrust, ThrustUnit.POUNDS) }
                ))
            } ?: emptyList(),
            registrantType = masterRecord.typeRegistrant?.let { RegistrantType.fromFaaCode(it) },
            registrant = parseRegistrant(
                masterRecord.name,
                masterRecord.street,
                masterRecord.street2,
                masterRecord.city,
                masterRecord.zipCode,
                masterRecord.state,
                masterRecord.country
            ),
            certificateIssueDate = parseDate(masterRecord.certIssueDate),
            lastActivityDate = parseDate(masterRecord.lastActionDate),
            expirationDate = parseDate(masterRecord.expirationDate),
            airworthiness = parseAirworthiness(masterRecord.certification, masterRecord.airWorthDate),
            fractionalOwnership = masterRecord.fractOwner == "Y",
            coOwners = parseCoOwners(
                masterRecord.otherNames1,
                masterRecord.otherNames2,
                masterRecord.otherNames3,
                masterRecord.otherNames4,
                masterRecord.otherNames5,
            )
        )
        return registration
    }

    private fun parseRegistrant(
        name: String?,
        street: String?,
        street2: String?,
        city: String?,
        zipCode: String?,
        state: String?,
        country: String?
    ): Registrant? =
        Registrant(name, parseAddress(street, street2, city, zipCode, state, country))
            .takeIf { it.address != null && it.name != null }

    private fun parseAddress(
        street: String?,
        street2: String?,
        city: String?,
        zipCode: String?,
        state: String?,
        country: String?
    ): Address? {
        if (street == null && street2 == null && city == null && zipCode == null && country == null) return null
        return Address(
            street1 = street,
            street2 = street2,
            city = city,
            state = fetchStateIfNull(state, zipCode),
            zipCode =  if (country == "US") parseUsZipCode(zipCode) else zipCode,
            country = country)
    }

    private fun fetchStateIfNull(state: String?, zipCode: String?): String? = when {
        !state.isNullOrBlank() -> state
        zipCode.isNullOrBlank() -> null
        else -> doFetchState(zipCode)
    }

    private fun doFetchState(zipCode: String): String? =
        if (zipCode.length < 5) null
        else zipCodeRepository.state(zipCode.substring(0, 5))

    private fun parseUsZipCode(zipCode: String?): String? = when {
        zipCode == null -> null
        zipCode.length < 6 -> zipCode
        else -> zipCode.substring(0, 5) + "-" + zipCode.substring(5)
    }

    private fun parseAirworthiness(certification: String?, airWorthDate: String?): Airworthiness? {
        var certificateClass: AirworthinessCertificateClass? = null
        var operation: List<AirworthinessOperation> = emptyList()
        certification?.let {
            if (it.isEmpty()) return null
            certificateClass = if (it.isEmpty()) null else it[0].digitToIntOrNull()?.let { classificationCode ->
                AirworthinessCertificateClass.fromFaaCode(classificationCode)
            }
            operation = if (it.length > 1 && certificateClass != null)
                AirworthinessOperation.fromFaaCode(it.substring(1), certificateClass!!)
            else emptyList()
        }
        val airworthinessDate = parseDate(airWorthDate)
        @Suppress("KotlinConstantConditions")
        return Airworthiness(certificateClass, operation, airworthinessDate).takeIf {
            airworthinessDate != null && certificateClass != null && operation.isNotEmpty()
        }
    }

    private fun parseCoOwners(name1: String?, name2: String?, name3: String?, name4: String?, name5: String?): List<String> {
        val coOwners = mutableListOf<String>()
        name1?.let { coOwners += it }
        name2?.let { coOwners += it }
        name3?.let { coOwners += it }
        name4?.let { coOwners += it }
        name5?.let { coOwners += it }
        return coOwners
    }

    private fun parseDate(str: String?): LocalDate? = str?.let { LocalDate.parse(str, dateFormatter) }

}

fun main(args: Array<String>) {
    require(args.isNotEmpty()) { "Usage: FaaRegistrationImporter ReleasableAircraft/" }
    val basePath = args[0]

    startKoin {
        modules(module {
            single { ZipCodeRepository() }
            single { FaaRegistrationImporter(basePath) }
        })

        val n12234 = koin.get<FaaRegistrationImporter>().import().first {
            it.registrationId.id == "N12234"
        }

        println(n12234)
    }
}