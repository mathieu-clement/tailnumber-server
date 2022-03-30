package com.edelweiss.software.tailnumber.server.core.registration

import com.edelweiss.software.tailnumber.server.core.serializers.UnstructuredRegistrantSerializer
import kotlinx.serialization.Serializable

@Serializable(with = UnstructuredRegistrantSerializer::class)
data class UnstructuredRegistrant(
    val value: String
) : Registrant()
