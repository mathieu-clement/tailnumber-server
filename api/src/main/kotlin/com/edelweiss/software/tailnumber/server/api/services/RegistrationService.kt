package com.edelweiss.software.tailnumber.server.api.services

import com.edelweiss.software.tailnumber.server.core.exceptions.RegistrantNotFoundException
import com.edelweiss.software.tailnumber.server.core.registration.PartialRegistration
import com.edelweiss.software.tailnumber.server.core.registration.Registration
import com.edelweiss.software.tailnumber.server.core.registration.RegistrationId
import com.edelweiss.software.tailnumber.server.repositories.RegistrationRepository
import com.edelweiss.software.tailnumber.server.search.elastic.RegistrationSearchService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RegistrationService : KoinComponent {

    private val registrationRepository by inject<RegistrationRepository>()
    private val searchService by inject<RegistrationSearchService>()

    // TODO client needs to automatically convert to uppercase and remove/not allow symbols and stuff, only A-Z1-9
    fun findByTailNumbers(tailNumbers: List<String>): List<Registration> =
        registrationRepository.findByTailNumbers(tailNumbers)

    fun findByRegistrationIds(registrationIds: List<RegistrationId>) : List<Registration> =
        registrationRepository.findByRegistrationIds(registrationIds)

    fun findByRegistrantNames(names: Set<String>) : List<PartialRegistration> =
        searchService.findByRegistrantNames(names).ifEmpty { throw RegistrantNotFoundException(names) }
}