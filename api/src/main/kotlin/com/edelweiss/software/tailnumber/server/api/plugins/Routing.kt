package com.edelweiss.software.tailnumber.server.api.plugins

import com.edelweiss.software.tailnumber.server.api.models.NotFoundErrorDTO
import com.edelweiss.software.tailnumber.server.api.services.RegistrationService
import com.edelweiss.software.tailnumber.server.core.Country
import com.edelweiss.software.tailnumber.server.core.exceptions.CountryNotFoundException
import com.edelweiss.software.tailnumber.server.core.exceptions.RegistrantNotFoundException
import com.edelweiss.software.tailnumber.server.core.exceptions.RegistrationsNotFoundException
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

    routing {
        get("/registrations") {
            val nameOrAddress = getNameOrAddressParam()
            val countries = getCountriesParam()
            call.respond(
                registrationService.findByRegistrantNameOrAddress(nameOrAddress.toSet(), countries.toSet()))
        }

        get("/registrations/autocomplete/{prefix}") {
            val prefix = call.parameters["prefix"]!!
            require(prefix.length >= 3) { "Requires at least 3 characters"}
            require("*" !in prefix) { "Wildcard not allowed" }
            call.respond(registrationService.autocompleteRegistrationId(prefix))
        }

        get("/registrations/full") {
            val names = getNameOrAddressParam()
            val partialRegs = registrationService.findByRegistrantNameOrAddress(names.toSet(), emptySet())
            call.respond(registrationService.findByRegistrationIds(partialRegs.map { it.registrationId }))
        }

        get("/registrations/{tailNumber}") {
            val tailNumber = call.parameters["tailNumber"]!!
            call.respond(registrationService.findByTailNumbers(listOf(tailNumber)))
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
            if (cause !is RegistrationsNotFoundException && cause !is CountryNotFoundException) {
                logger.error("API error", cause)
            } else {
                logger.trace("Casual exception", cause)
            }
        }
    }
}

private fun PipelineContext<Unit, ApplicationCall>.getNameOrAddressParam(): List<String> {
    val names = call.request.queryParameters["name_or_address"]?.split(",")
    require(names != null && names.isNotEmpty() && names[0].isNotEmpty()) { "Parameter missing or empty" }
    require(names[0] != "*") { "Wildcard is not allowed" }
    return names
}

private fun PipelineContext<Unit, ApplicationCall>.getCountriesParam(): List<Country> {
    return call.request.queryParameters["countries"]
        .takeIf { !it.isNullOrEmpty() }
        ?.split(",")
        ?.map { Country.valueOf(it) }
        ?: emptyList()
}