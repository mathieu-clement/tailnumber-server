package com.edelweiss.software.tailnumber.server.api.services

import com.edelweiss.software.tailnumber.server.core.Country
import com.edelweiss.software.tailnumber.server.core.exceptions.RegistrantNotFoundException
import com.edelweiss.software.tailnumber.server.core.registration.PartialRegistration
import com.edelweiss.software.tailnumber.server.core.registration.RegistrationId
import com.edelweiss.software.tailnumber.server.core.registration.RegistrationResult
import com.edelweiss.software.tailnumber.server.repositories.RegistrationRepository
import com.edelweiss.software.tailnumber.server.search.elastic.ElasticRegistrationSearchService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RegistrationService : KoinComponent {

    private val registrationRepository by inject<RegistrationRepository>()
    private val searchService by inject<ElasticRegistrationSearchService>()

    fun findByTailNumbers(tailNumbers: List<String>): List<RegistrationResult> =
        registrationRepository.findByTailNumbers(tailNumbers)

    fun findByRegistrationIds(registrationIds: List<RegistrationId>) : List<RegistrationResult> =
        registrationRepository.findByRegistrationIds(registrationIds)

    fun findByRegistrantNameOrAddress(names: Set<String>, country: Country?, exact: Boolean) : List<PartialRegistration> =
        searchService.findByRegistrantNameOrAddress(names, country, exact)
            .ifEmpty { throw RegistrantNotFoundException(names) }

    fun autocompleteRegistrationId(prefix: String) : List<PartialRegistration> =
        searchService.autocompleteRegistration(prefix)
}