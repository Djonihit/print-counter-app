package com.printcounter.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "print_records")
data class PrintRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val quantity: Int = 1,
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val deviceName: String = ""
)
