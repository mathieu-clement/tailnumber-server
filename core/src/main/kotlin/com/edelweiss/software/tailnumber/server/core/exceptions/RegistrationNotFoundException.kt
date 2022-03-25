package com.edelweiss.software.tailnumber.server.core.exceptions

import com.edelweiss.software.tailnumber.server.core.registration.RegistrationId

class RegistrationNotFoundException(val registrationId: RegistrationId)
    : Exception("Registration not found: $registrationId")