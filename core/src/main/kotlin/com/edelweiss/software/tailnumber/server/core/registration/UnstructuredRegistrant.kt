package com.edelweiss.software.tailnumber.server.core.registration

import com.edelweiss.software.tailnumber.server.core.serializers.UnstructuredRegistrantSerializer

@kotlinx.serialization.Serializable(with = UnstructuredRegistrantSerializer::class)
data class UnstructuredRegistrant(
    val value: String
) : Registrant()
