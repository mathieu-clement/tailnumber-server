package com.edelweiss.software.tailnumber.server.api.services

import com.edelweiss.software.tailnumber.server.core.exceptions.RegistrantNotFoundException
import com.edelweiss.software.tailnumber.server.core.registration.PartialRegistration
import com.edelweiss.software.tailnumber.server.core.registration.Registration
import com.edelweiss.software.tailnumber.server.repositories.RegistrationRepository
import com.edelweiss.software.tailnumber.server.search.elastic.RegistrationSearchService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RegistrationService : KoinComponent {

    private val registrationRepository by inject<RegistrationRepository>()
    private val searchService by inject<RegistrationSearchService>()

    // TODO client needs to automatically convert to uppercase and remove/not allow symbols and stuff, only A-Z1-9
    fun findByTailNumber(tailNumber: String): Registration = registrationRepository.findByRegistrationId(tailNumber)

    fun findByRegistrantNames(names: Set<String>) : List<PartialRegistration> =
        searchService.findByRegistrantNames(names).ifEmpty { throw RegistrantNotFoundException(names) }
}