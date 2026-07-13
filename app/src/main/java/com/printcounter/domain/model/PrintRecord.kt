package com.printcounter.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class PrintFormat {
    A2, A3, A4, A5, A6
}

enum class EmbroideryFormat {
    A3, A4, A5, A6
}

enum class WorkType {
    PRINT, EMBROIDERY
}

@Entity(tableName = "print_records")
data class PrintRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val quantity: Int = 1,
    val format: String, // PrintFormat или EmbroideryFormat
    val workType: String, // PRINT или EMBROIDERY
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val deviceName: String = ""
)

data class FormatStatistics(
    val format: String,
    val totalQuantity: Int,
    val recordCount: Int,
    val workType: String
)
