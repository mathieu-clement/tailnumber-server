package com.edelweiss.software.tailnumber.server.core.exceptions

class RegistrantNotFoundException(val names: Set<String>)
    : Exception("Registrant(s) not found: $names")