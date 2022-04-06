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
    @SerialName("registrant.address.street1") val registrantAddressStreet1: String? = null,

    @Serializable(with = StringFieldValueUnwrapperSerializer::class)
    @SerialName("registrant.address.street2") val registrantAddressStreet2: String? = null,

    @Serializable(with = StringFieldValueUnwrapperSerializer::class)
    @SerialName("registrant.address.poBox") val registrantPoBox: String? = null,
    
    @Serializable(with = StringFieldValueUnwrapperSerializer::class)
    @SerialName("registrant.address.city") val registrantAddressCity: String? = null,

    @Serializable(with = StringFieldValueUnwrapperSerializer::class)
    @SerialName("registrant.address.state") val registrantAddressState: String? = null,

    @Serializable(with = StringFieldValueUnwrapperSerializer::class)
    @SerialName("registrant.address.zipCode") val registrantAddressZipCode: String? = null,

    @Serializable(with = StringFieldValueUnwrapperSerializer::class)
    @SerialName("registrant.address.country") val registrantAddressCountry: String? = null,

    @Serializable(with = StringFieldValueUnwrapperSerializer::class)
    @SerialName("owner.name") val ownerName: String? = null,

    @Serializable(with = StringFieldValueUnwrapperSerializer::class)
    @SerialName("owner.address.street1") val ownerAddressStreet1: String? = null,

    @Serializable(with = StringFieldValueUnwrapperSerializer::class)
    @SerialName("owner.address.street2") val ownerAddressStreet2: String? = null,

    @Serializable(with = StringFieldValueUnwrapperSerializer::class)
    @SerialName("owner.address.poBox") val ownerPoBox: String? = null,

    @Serializable(with = StringFieldValueUnwrapperSerializer::class)
    @SerialName("owner.address.city") val ownerAddressCity: String? = null,

    @Serializable(with = StringFieldValueUnwrapperSerializer::class)
    @SerialName("owner.address.state") val ownerAddressState: String? = null,

    @Serializable(with = StringFieldValueUnwrapperSerializer::class)
    @SerialName("owner.address.zipCode") val ownerAddressZipCode: String? = null,

    @Serializable(with = StringFieldValueUnwrapperSerializer::class)
    @SerialName("owner.address.country") val ownerAddressCountry: String? = null,

    @Serializable(with = StringFieldValueUnwrapperSerializer::class)
    @SerialName("operator.name") val operatorName: String? = null,

    @Serializable(with = StringFieldValueUnwrapperSerializer::class)
    @SerialName("operator.address.street1") val operatorAddressStreet1: String? = null,

    @Serializable(with = StringFieldValueUnwrapperSerializer::class)
    @SerialName("operator.address.street2") val operatorAddressStreet2: String? = null,

    @Serializable(with = StringFieldValueUnwrapperSerializer::class)
    @SerialName("operator.address.poBox") val operatorPoBox: String? = null,

    @Serializable(with = StringFieldValueUnwrapperSerializer::class)
    @SerialName("operator.address.city") val operatorAddressCity: String? = null,

    @Serializable(with = StringFieldValueUnwrapperSerializer::class)
    @SerialName("operator.address.state") val operatorAddressState: String? = null,

    @Serializable(with = StringFieldValueUnwrapperSerializer::class)
    @SerialName("operator.address.zipCode") val operatorAddressZipCode: String? = null,

    @Serializable(with = StringFieldValueUnwrapperSerializer::class)
    @SerialName("operator.address.country") val operatorAddressCountry: String? = null,

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
