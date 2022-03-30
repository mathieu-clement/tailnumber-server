package com.edelweiss.software.tailnumber.server.search.elastic.dto.response

import com.edelweiss.software.tailnumber.server.search.elastic.serializers.IntFieldValueUnwrapperSerializer
import com.edelweiss.software.tailnumber.server.search.elastic.serializers.StringFieldValueUnwrapperSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FieldsResponse(
    @Serializable(with = StringFieldValueUnwrapperSerializer::class)
    @SerialName("registrant.name") val registrantName: String? = null,

    @Serializable(with = StringFieldValueUnwrapperSerializer::class)
    val owner: String? = null,

    @Serializable(with = StringFieldValueUnwrapperSerializer::class)
    val operator: String? = null,

    @Serializable(with = StringFieldValueUnwrapperSerializer::class)
    @SerialName("registrant.address.street1") val registrantAddressStreet1: String? = null,

    @Serializable(with = StringFieldValueUnwrapperSerializer::class)
    @SerialName("registrant.address.street2") val registrantAddressStreet2: String? = null,

    @Serializable(with = StringFieldValueUnwrapperSerializer::class)
    @SerialName("registrant.address.city") val registrantAddressCity: String? = null,

    @Serializable(with = StringFieldValueUnwrapperSerializer::class)
    @SerialName("registrant.address.state") val registrantAddressState: String? = null,

    @Serializable(with = StringFieldValueUnwrapperSerializer::class)
    @SerialName("registrant.address.zipCode") val registrantAddressZipCode: String? = null,

    @Serializable(with = StringFieldValueUnwrapperSerializer::class)
    @SerialName("aircraftReference.manufacturer") val aircraftReferenceManufacturer : String? = null,

    @Serializable(with = StringFieldValueUnwrapperSerializer::class)
    @SerialName("aircraftReference.model") val aircraftReferenceModel : String? = null,

    @Serializable(with = IntFieldValueUnwrapperSerializer::class)
    @SerialName("aircraftReference.manufactureYear") val aircraftReferenceManufactureYear : Int? = null,

    @Serializable(with = StringFieldValueUnwrapperSerializer::class)
    @SerialName("registrationId.id") val registrationIdId : String,

    @Serializable(with = StringFieldValueUnwrapperSerializer::class)
    @SerialName("registrationId.country") val registrationIdCountry : String
)
