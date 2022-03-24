package com.edelweiss.software.tailnumber.server.importer.faa.acftref

data class AcftRefRecord(
    val code: String,
    val mfr: String,
    val model: String,
    val typeAcft: Char?, // TYPE-ACFT
    val typeEng: Int, // TYPE-ENG
    val acCat: Int, // etc.
    val buildCertInd: Int,
    val noEng: Int,
    val noSeats: Int,
    val acWeight: Int,
    val speed: Int?
)
