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
                PolymorphicEnumSerializer(AirworthinessCertificateClass.serializer())
            )
            subclass(
                StandardAirworthinessOperation::class,
                PolymorphicEnumSerializer(StandardAirworthinessOperation.serializer())
            )
            subclass(
                RestrictedAirworthinessOperation::class,
                PolymorphicEnumSerializer(RestrictedAirworthinessOperation.serializer())
            )
            subclass(
                ExperimentalAirworthinessOperation::class, PolymorphicEnumSerializer(
                    ExperimentalAirworthinessOperation.serializer()
                )
            )
            subclass(
                ProvisionalAirworthinessOperation::class, PolymorphicEnumSerializer(
                    ProvisionalAirworthinessOperation.serializer()
                )
            )
            subclass(
                SpecialFlightPermitAirworthinessOperation::class, PolymorphicEnumSerializer(
                    SpecialFlightPermitAirworthinessOperation.serializer()
                )
            )
            subclass(
                LightSportAirworthinessOperation::class,
                PolymorphicEnumSerializer(LightSportAirworthinessOperation.serializer())
            )
        }
    }
}