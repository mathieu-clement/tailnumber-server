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
            val exact = getExact()
            val nameOrAddress = getNameOrAddressParam(exact)
            call.respond(
                registrationService.findByRegistrantNameOrAddress(nameOrAddress.toSet(), getCountry(), exact))
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
            call.respond(registrationService.autocompleteRegistrationId(prefix))
        }

        get("/registrations/{tailNumber}") {
            val tailNumber = call.parameters["tailNumber"]!!
            call.respond(registrationService.findByTailNumbers(listOf(tailNumber))
                .firstOrNull() ?: throw RegistrantNotFoundException(setOf(tailNumber)))
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