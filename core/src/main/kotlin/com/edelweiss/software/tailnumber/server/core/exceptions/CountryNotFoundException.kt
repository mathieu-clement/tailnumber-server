package com.edelweiss.software.tailnumber.server.core.exceptions

class CountryNotFoundException(val rawRegistrationId: String)
    : Exception("Unknown country for registration: $rawRegistrationId") {
}