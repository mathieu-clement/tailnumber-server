package com.edelweiss.software.tailnumber.server.core.airworthiness

import java.time.LocalDate

data class Airworthiness(
    val certificateClass: AirworthinessCertificateClass,
    val approvedOperation: AirworthinessOperation,
    val airworthinessDate: LocalDate
)
