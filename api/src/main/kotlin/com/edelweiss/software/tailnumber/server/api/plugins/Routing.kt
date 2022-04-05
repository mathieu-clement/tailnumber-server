package com.edelweiss.software.tailnumber.server.api.plugins

import com.edelweiss.software.tailnumber.server.api.models.NotFoundErrorDTO
import com.edelweiss.software.tailnumber.server.api.services.RegistrationService
import com.edelweiss.software.tailnumber.server.core.Country
import com.edelweiss.software.tailnumber.server.core.exceptions.CountryNotFoundException
import com.edelweiss.software.tailnumber.server.core.exceptions.RegistrantNotFoundException
import com.edelweiss.software.tailnumber.server.core.exceptions.RegistrationsNotFoundException
import com.edelweiss.software.tailnumber.server.core.registration.RegistrationId
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import org.koin.ktor.ext.inject
import org.slf4j.LoggerFactory

val logger = LoggerFactory.getLogger("com.edelweiss.software.tailnumber.server.api.plugins.Routing")

fun Application.configureRouting() {
    install(IgnoreTrailingSlash)
    install(AutoHeadResponse)

    val registrationService by inject<RegistrationService>()

    /*
    val tailnumberRegexes : List<Regex> = listOf(
        "N[1-9][0-9]{1,4}", "N[1-9][0-9]{1,3}[A-Z]", "N[1-9][0-9]{1,2}[A-Z]{2}",
        "HB-?[A-Z0-9]{3,4}"
    ).map { Regex(it) }
     */
    val tailnumberPrefixRegexes : List<Regex> = listOf(
        "N[1-9][0-9A-Z]+",
        "HB-?[A-Z0-9]+"
    ).map { Regex(it) }

    routing {
        get("/registrations") {
            val exact = getExact()
            val nameOrAddress = getNameOrAddressParam(exact)
            val country = getCountry()
            callAutocompleteRegistrant(registrationService, nameOrAddress, country, exact)
        }

        get("/registrations/full") {
            val exact = getExact()
            val names = getNameOrAddressParam(exact)
            val partialRegs = registrationService.findByRegistrantNameOrAddress(names.toSet(), getCountry(), exact)
            call.respond(registrationService.findByRegistrationIds(partialRegs.map { it.registrationId }))
        }

        get("/registrations/autocomplete/{prefix}") {
            val prefix = call.parameters["prefix"]!!
            require(prefix.length >= 3) { "Requires at least 3 characters"}
            require("*" !in prefix) { "Wildcard not allowed" }
            callAutocompleteTailNumber(registrationService, prefix)
        }

        get("/registrations/any/{searchText}") {
            val searchText = call.parameters["searchText"]!!
            require(searchText.length >= 3) { "Requires at least 3 characters"}
            require("*" !in searchText) { "Wildcard not allowed" }
            val uppercase = searchText.uppercase()
            val isTailNumber = when {
                searchText.split(" ").size > 1 -> false
                tailnumberPrefixRegexes.any { it.matchEntire(uppercase) != null } -> true
                else -> false
            }
            if (isTailNumber) {
                callAutocompleteTailNumber(registrationService, uppercase)
            } else {
                callAutocompleteRegistrant(registrationService, listOf(searchText), null, false)
            }
        }

        get("/registrations/{tailNumber}") {
            val tailNumber = call.parameters["tailNumber"]!!
            call.respond(registrationService.findByTailNumbers(listOf(tailNumber))
                .firstOrNull() ?: throw RegistrationsNotFoundException(listOf(RegistrationId.fromTailNumber(tailNumber))))
        }

        get("/registrations/{tailNumber}/extras") {

        }
    }

    install(StatusPages) {
        exception<Throwable> { cause ->
            when (cause) {
                is IllegalArgumentException ->
                    call.respond(HttpStatusCode.BadRequest, cause.message ?: "Illegal argument")
                is NullPointerException ->
                    call.respond(HttpStatusCode.BadRequest, "Bad Request: ${cause.message}")
                is RegistrationsNotFoundException ->
                    call.respond(HttpStatusCode.NotFound,
                        NotFoundErrorDTO("tailNumbers", cause.registrationIds.joinToString(", ") { it.id }))
                is RegistrantNotFoundException ->
                    call.respond(HttpStatusCode.NotFound,
                        NotFoundErrorDTO("registrants", cause.names.joinToString(", ")))
                is CountryNotFoundException ->
                    call.respond(HttpStatusCode.NotFound,
                        NotFoundErrorDTO("country", cause.rawRegistrationId))
                else ->
                    call.respond(HttpStatusCode.InternalServerError, "Internal Server Error: ${cause.message}")
            }
            if (cause !is RegistrationsNotFoundException &&
                cause !is CountryNotFoundException &&
                cause !is RegistrantNotFoundException) {
                logger.error("API error", cause)
            } else {
                logger.info("Casual exception: ${cause.message}")
            }
        }
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.callAutocompleteRegistrant(
    registrationService: RegistrationService,
    nameOrAddress: List<String>,
    country: Country?,
    exact: Boolean
) {
    call.respond(
        registrationService.findByRegistrantNameOrAddress(nameOrAddress.toSet(), country, exact)
    )
}

private suspend fun PipelineContext<Unit, ApplicationCall>.callAutocompleteTailNumber(
    registrationService: RegistrationService,
    prefix: String
) {
    call.respond(registrationService.autocompleteRegistrationId(prefix))
}

private fun PipelineContext<Unit, ApplicationCall>.getNameOrAddressParam(exact: Boolean): List<String> {
    val nameOrAddress = call.request.queryParameters["name_or_address"]!!
    require(nameOrAddress.isNotEmpty()) { "Parameter missing or empty" }
    require("*" !in nameOrAddress) { "Wildcard is not allowed" }
    return if (exact) listOf(nameOrAddress) else nameOrAddress.split(",")
}

private fun PipelineContext<Unit, ApplicationCall>.getCountry(): Country? {
    return call.request.queryParameters["country"]
        .takeIf { !it.isNullOrEmpty() }
        ?.let { Country.valueOf(it) }
}

private fun PipelineContext<Unit, ApplicationCall>.getExact(): Boolean {
    return call.request.queryParameters["exact"].toBoolean()
}