package com.edelweiss.software.tailnumber.server.extras

import com.edelweiss.software.tailnumber.server.core.registration.RegistrationId
import com.edelweiss.software.tailnumber.server.extras.models.Extras
import com.edelweiss.software.tailnumber.server.extras.models.Picture

class ExtrasRepository {
    fun getExtras(registrationId: RegistrationId): Extras {
        return Extras(
            mainPicture = Picture("http://www.jetphotos.com/blabla")
        )
    }
}