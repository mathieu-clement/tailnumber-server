package com.edelweiss.software.tailnumber.server.api.plugins

import com.edelweiss.software.tailnumber.server.core.aircraft.WeightCategory
import com.edelweiss.software.tailnumber.server.core.airworthiness.*
import com.edelweiss.software.tailnumber.server.core.registration.TransponderCode
import com.edelweiss.software.tailnumber.server.core.serializers.LocalDateSerializer
import com.edelweiss.software.tailnumber.server.core.serializers.PolymorphicEnumSerializer
import com.edelweiss.software.tailnumber.server.core.serializers.TransponderCodeSerializer
import com.edelweiss.software.tailnumber.server.core.serializers.WeightCategorySerializer
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import java.time.LocalDate

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
//            prettyPrint = true
            serializersModule = SerializersModule {
                contextual(LocalDate::class) { LocalDateSerializer }
                contextual(WeightCategory::class) { WeightCategorySerializer }
                contextual(TransponderCode::class) { TransponderCodeSerializer }
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
        })
    }
}