package com.edelweiss.software.tailnumber.server.api.plugins

import com.edelweiss.software.tailnumber.server.api.models.NotFoundErrorDTO
import com.edelweiss.software.tailnumber.server.api.services.RegistrationService
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
        get("/registrations/{tailNumber}") {
            val tailNumber = call.parameters["tailNumber"]!!
            call.respond(registrationService.findByTailNumbers(listOf(tailNumber)))
        }

        get("/registrations") {
            val names = getRegistrantsParam()
            call.respond(registrationService.findByRegistrantNames(names.toSet()))
        }

        get("/registrations/full") {
            val names = getRegistrantsParam()
            val partialRegs = registrationService.findByRegistrantNames(names.toSet())
            call.respond(registrationService.findByRegistrationIds(partialRegs.map { it.registrationId }))
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

private fun PipelineContext<Unit, ApplicationCall>.getRegistrantsParam(): List<String> {
    val names = call.request.queryParameters["registrants"]?.split(",")
    require(names != null && names.isNotEmpty() && names[0].isNotEmpty()) { "Parameter missing or empty" }
    require(names[0] != "*") { "Wildcard is not allowed" }
    return names
}