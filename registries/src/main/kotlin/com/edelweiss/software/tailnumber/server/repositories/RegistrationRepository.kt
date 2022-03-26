package com.edelweiss.software.tailnumber.server.repositories

import com.edelweiss.software.tailnumber.server.core.registration.Registration
import com.edelweiss.software.tailnumber.server.core.registration.RegistrationId

interface RegistrationRepository {
//    fun getAll() : Sequence<Registration>

    fun findByTailNumbers(tailNumbers: List<String>) =
        findByRegistrationIds(tailNumbers.map { RegistrationId.fromTailNumber(it) })

    fun findByRegistrationIds(registrationIds: List<RegistrationId>) : List<Registration>
}