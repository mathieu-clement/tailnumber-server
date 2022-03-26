package com.edelweiss.software.tailnumber.server.importer.elastic

import com.edelweiss.software.tailnumber.server.core.registration.Registration
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class UpsertDoc(
    val doc: Registration,
    @EncodeDefault @SerialName("doc_as_upsert") val docAsUpsert : Boolean = true
)
