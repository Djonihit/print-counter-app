package com.printcounter.domain.model

data class DailyStatistics(
    val date: Long,
    val totalPrints: Int,
    val totalEmbroidery: Int,
    val printRecordCount: Int,
    val embroideryRecordCount: Int,
    val formatStatistics: List<FormatStatistics> = emptyList()
)
