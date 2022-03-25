package com.edelweiss.software.tailnumber.server.api.services

import com.edelweiss.software.tailnumber.server.repositories.RegistrationRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RegistrationService : KoinComponent {

    private val registrationRepository by inject<RegistrationRepository>()

    fun findByTailNumber(tailNumber: String) = registrationRepository.findByRegistrationId(tailNumber)
}