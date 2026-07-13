package com.printcounter.domain.model

data class DailyStatistics(
    val date: Long,
    val totalPrints: Int,
    val recordCount: Int,
    val averagePerRecord: Double
)
