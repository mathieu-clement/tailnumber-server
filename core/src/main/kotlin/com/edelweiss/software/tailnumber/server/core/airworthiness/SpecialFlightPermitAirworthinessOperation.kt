package com.edelweiss.software.tailnumber.server.core.airworthiness

import org.slf4j.LoggerFactory

enum class SpecialFlightPermitAirworthinessOperation : AirworthinessOperation {
    FERRY_FLIGHT_FOR_REPAIRS_ALTERATIONS_MAINTENANCE_OR_STORAGE,
    EVACUATE_FROM_AREA_OF_IMPENDING_DANGER,
    OPERATION_IN_EXCESS_OF_MAXIMUM_CERTIFICATED,
    DELIVERY_OF_EXPORT,
    PRODUCTION_FLIGHT_TESTING,
    CUSTOMER_DEMO
    ;
    
    companion object {
        private val logger = LoggerFactory.getLogger(SpecialFlightPermitAirworthinessOperation::class.java)

        fun fromFaaCodes(codes: String) : List<SpecialFlightPermitAirworthinessOperation> =
            codes
                .toCharArray()
                .map { it.digitToInt() }
                .mapNotNull { fromFaaCode(it) }
                .toList()
        
        private fun fromFaaCode(code: Int) = when (code) {
            1 -> FERRY_FLIGHT_FOR_REPAIRS_ALTERATIONS_MAINTENANCE_OR_STORAGE
            2 -> EVACUATE_FROM_AREA_OF_IMPENDING_DANGER
            3 -> OPERATION_IN_EXCESS_OF_MAXIMUM_CERTIFICATED
            4 -> DELIVERY_OF_EXPORT
            5 -> PRODUCTION_FLIGHT_TESTING
            6 -> CUSTOMER_DEMO
            else -> {
                logger.warn("Unknown special flight permit airworthiness operation: $code")
                null
            }
        }
    }
}
