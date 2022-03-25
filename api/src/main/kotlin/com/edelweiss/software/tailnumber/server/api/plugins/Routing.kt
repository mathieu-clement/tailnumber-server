package com.edelweiss.software.tailnumber.server.api.plugins

import com.edelweiss.software.tailnumber.server.api.models.NotFoundErrorDTO
import com.edelweiss.software.tailnumber.server.api.services.RegistrationService
import com.edelweiss.software.tailnumber.server.core.exceptions.CountryNotFoundException
import com.edelweiss.software.tailnumber.server.core.exceptions.RegistrationNotFoundException
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import org.slf4j.LoggerFactory

val logger = LoggerFactory.getLogger("com.edelweiss.software.tailnumber.server.api.plugins.Routing")

fun Application.configureRouting() {
    install(IgnoreTrailingSlash)

    val registrationService by inject<RegistrationService>()

    routing {
        get("/registrations/{tailNumber}") {
            val tailNumber = call.parameters["tailNumber"]!!
            call.respond(registrationService.findByTailNumber(tailNumber))
        }
    }

    install(StatusPages) {
        exception<Throwable> { cause ->
            when (cause) {
                is IllegalArgumentException ->
                    call.respond(HttpStatusCode.BadRequest, cause.message ?: "Illegal argument")
                is NullPointerException ->
                    call.respond(HttpStatusCode.BadRequest, "Bad Request: ${cause.message}")
                is RegistrationNotFoundException ->
                    call.respond(HttpStatusCode.NotFound,
                        NotFoundErrorDTO("tailNumber", cause.registrationId.id))
                is CountryNotFoundException ->
                    call.respond(HttpStatusCode.NotFound,
                        NotFoundErrorDTO("country", cause.rawRegistrationId))
                else ->
                    call.respond(HttpStatusCode.InternalServerError, "Internal Server Error: ${cause.message}")
            }
            if (cause !is RegistrationNotFoundException && cause !is CountryNotFoundException) {
                logger.error("API error", cause)
            } else {
                logger.trace("Casual exception", cause)
            }
        }
    }
}