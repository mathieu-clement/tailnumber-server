package com.edelweiss.software.tailnumber.server.core.exceptions

import com.edelweiss.software.tailnumber.server.core.registration.RegistrationId

class RegistrationsNotFoundException(val registrationIds: List<RegistrationId>)
    : Exception("Registrations not found: $registrationIds")