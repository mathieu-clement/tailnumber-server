package com.edelweiss.software.tailnumber.server.repositories

import com.edelweiss.software.tailnumber.server.core.registration.RegistrationId
import com.edelweiss.software.tailnumber.server.core.registration.RegistrationResult

interface RegistrationRepository {
    fun findByTailNumbers(tailNumbers: List<String>) =
        findByRegistrationIds(tailNumbers.map { RegistrationId.fromTailNumber(it) })

    fun findByRegistrationIds(registrationIds: List<RegistrationId>) : List<RegistrationResult>
}