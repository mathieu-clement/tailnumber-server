package com.edelweiss.software.tailnumber.server.core.airworthiness

import org.slf4j.LoggerFactory

enum class ExperimentalAirworthinessOperation : AirworthinessOperation {
    TO_SHOW_COMPLIANCE_WITH_FAR,
    RESEARCH_AND_DEVELOPMENT,
    AMATEUR_BUILT,
    EXHIBITION,
    RACING,
    CREW_TRAINING,
    MARKET_SURVEY,
    OPERATING_KIT_BUILT_AIRCRAFT,
    REG_PRIOR_TO_20080131,
    OPERATING_LIGHT_SPORT_KIT_BUILT,
    OPERATING_LIGHT_SPORT_21_190,
    UNMANNED_AIRCRAFT_RESEARCH_AND_DEVELOPMENT,
    UNMANNED_AIRCRAFT_CREW_TRAINING,
    UNMANNED_AIRCRAFT_MARKET_SURVEY,
    UNMANNED_AIRCRAFT_EXHIBITION,
    UNMANNED_AIRCRAFT_COMPLIANCE_WITH_CFR
    ;

    companion object {
        private val logger = LoggerFactory.getLogger(ExperimentalAirworthinessOperation::class.java)

        fun fromFaaCodes(codes: String) : List<ExperimentalAirworthinessOperation> =
            tokenize(codes).mapNotNull { fromFaaCode(it) }.toList()

        fun tokenize(codes: String) : Sequence<String> = sequence {
            var code = ""
            for (c in codes.trim().toCharArray()) {
                if (c in 'A'..'Z') {
                    if (code.isEmpty()) {
                        logger.warn("Invalid data, code starts with letter")
                    } else {
                        code += c
                    }
                } else {
                    // Process last code seen
                    if (code.isNotEmpty()) yield(code)

                    // Reset to new code
                    code = "" + c
                }
            }

            if (code.isNotEmpty()) {
                yield(code)
            }
        }

        private fun fromFaaCode(code: String) = when (code) {
            "0" -> TO_SHOW_COMPLIANCE_WITH_FAR
            "1" -> RESEARCH_AND_DEVELOPMENT
            "2" -> AMATEUR_BUILT
            "3" -> EXHIBITION
            "4" -> RACING
            "5" -> CREW_TRAINING
            "6" -> MARKET_SURVEY
            "7" -> OPERATING_KIT_BUILT_AIRCRAFT
            "8A" -> REG_PRIOR_TO_20080131
            "8B" -> OPERATING_LIGHT_SPORT_KIT_BUILT
            "8C" -> OPERATING_LIGHT_SPORT_21_190
            "9A" -> UNMANNED_AIRCRAFT_RESEARCH_AND_DEVELOPMENT
            "9B" -> UNMANNED_AIRCRAFT_MARKET_SURVEY
            "9C" -> UNMANNED_AIRCRAFT_CREW_TRAINING
            "9D" -> UNMANNED_AIRCRAFT_EXHIBITION
            "9E" -> UNMANNED_AIRCRAFT_COMPLIANCE_WITH_CFR
            else -> {
                logger.warn("Unknown experimental airworthiness operation: $code")
                null
            }
        }
    }
}
