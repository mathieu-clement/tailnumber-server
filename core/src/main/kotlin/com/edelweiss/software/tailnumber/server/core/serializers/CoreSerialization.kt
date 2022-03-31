package com.edelweiss.software.tailnumber.server.core.serializers

import com.edelweiss.software.tailnumber.server.core.aircraft.WeightCategory
import com.edelweiss.software.tailnumber.server.core.airworthiness.*
import com.edelweiss.software.tailnumber.server.core.registration.TransponderCode
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import java.time.LocalDate

object CoreSerialization {
    val serializersModule = SerializersModule {
        contextual(LocalDate::class) { LocalDateSerializer }
        contextual(WeightCategory::class) { WeightCategory.serializer() }
        contextual(TransponderCode::class) { TransponderCode.serializer() }
        polymorphic(AirworthinessOperation::class) {
            subclass(
                AirworthinessCertificateClass::class,
                PolymorphicEnumSerializer(AirworthinessCertificateClass.serializer(), false)
            )
            subclass(
                StandardAirworthinessOperation::class,
                PolymorphicEnumSerializer(StandardAirworthinessOperation.serializer(), false)
            )
            subclass(
                RestrictedAirworthinessOperation::class,
                PolymorphicEnumSerializer(RestrictedAirworthinessOperation.serializer(), false)
            )
            subclass(
                ExperimentalAirworthinessOperation::class, PolymorphicEnumSerializer(
                    ExperimentalAirworthinessOperation.serializer(), false
                )
            )
            subclass(
                ProvisionalAirworthinessOperation::class, PolymorphicEnumSerializer(
                    ProvisionalAirworthinessOperation.serializer(), false
                )
            )
            subclass(
                SpecialFlightPermitAirworthinessOperation::class, PolymorphicEnumSerializer(
                    SpecialFlightPermitAirworthinessOperation.serializer(), false
                )
            )
            subclass(
                LightSportAirworthinessOperation::class,
                PolymorphicEnumSerializer(LightSportAirworthinessOperation.serializer(), false)
            )
        }
    }
}