package com.edelweiss.software.tailnumber.server.repositories

import com.edelweiss.software.tailnumber.server.core.registration.Registration
import com.edelweiss.software.tailnumber.server.core.registration.RegistrationId

interface RegistrationRepository {
//    fun getAll() : Sequence<Registration>

    fun findByRegistrationId(tailNumber: String) =
        findByRegistrationId(RegistrationId.fromTailNumber(tailNumber))

    fun findByRegistrationId(registrationId: RegistrationId) : Registration
}