package com.edelweiss.software.tailnumber.server.core.serializers

import com.edelweiss.software.tailnumber.server.core.airworthiness.*
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import java.time.LocalDate

object CoreSerialization {
    val serializersModule = SerializersModule {
        contextual(LocalDate::class) { LocalDateSerializer }
        polymorphic(AirworthinessOperation::class) {
            subclass(
                AirworthinessCertificateClass::class,
                PolymorphicEnumSerializer(AirworthinessCertificateClass.serializer(), true)
            )
            subclass(
                StandardAirworthinessOperation::class,
                PolymorphicEnumSerializer(StandardAirworthinessOperation.serializer(), true)
            )
            subclass(
                RestrictedAirworthinessOperation::class,
                PolymorphicEnumSerializer(RestrictedAirworthinessOperation.serializer(), true)
            )
            subclass(
                ExperimentalAirworthinessOperation::class, PolymorphicEnumSerializer(
                    ExperimentalAirworthinessOperation.serializer(), true
                )
            )
            subclass(
                ProvisionalAirworthinessOperation::class, PolymorphicEnumSerializer(
                    ProvisionalAirworthinessOperation.serializer(), true
                )
            )
            subclass(
                SpecialFlightPermitAirworthinessOperation::class, PolymorphicEnumSerializer(
                    SpecialFlightPermitAirworthinessOperation.serializer(), true
                )
            )
            subclass(
                LightSportAirworthinessOperation::class,
                PolymorphicEnumSerializer(LightSportAirworthinessOperation.serializer(), true)
            )
        }
    }
}