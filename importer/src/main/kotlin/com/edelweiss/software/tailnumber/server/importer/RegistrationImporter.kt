package com.edelweiss.software.tailnumber.server.importer

import com.edelweiss.software.tailnumber.server.core.registration.Registration

interface RegistrationImporter {
    fun import(): List<Registration>
}