package com.edelweiss.software.tailnumber.server.core.registration

import com.edelweiss.software.tailnumber.server.core.Address

data class Registrant(
    val name: String,
    val address: Address
)
