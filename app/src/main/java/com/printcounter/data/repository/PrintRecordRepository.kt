package com.printcounter.data.repository

import com.printcounter.data.dao.PrintRecordDao
import com.printcounter.domain.model.DailyStatistics
import com.printcounter.domain.model.PrintRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class PrintRecordRepository(private val printRecordDao: PrintRecordDao) {
    
    fun getAllRecords(): Flow<List<PrintRecord>> = printRecordDao.getAllRecords()
    
    fun getRecentRecords(limit: Int): Flow<List<PrintRecord>> = printRecordDao.getRecentRecords(limit)
    
    suspend fun addPrintRecord(quantity: Int, description: String = "", deviceName: String = ""): Long {
        val record = PrintRecord(
            quantity = quantity,
            description = description,
            deviceName = deviceName,
            timestamp = System.currentTimeMillis()
        )
        return printRecordDao.insert(record)
    }
    
    suspend fun updatePrintRecord(record: PrintRecord) {
        printRecordDao.update(record)
    }
    
    suspend fun deletePrintRecord(record: PrintRecord) {
        printRecordDao.delete(record)
    }
    
    fun getTotalPrints(): Flow<Int> = printRecordDao.getTotalPrints()
    
    suspend fun getDailyStatistics(date: LocalDate): DailyStatistics {
        val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        
        val totalPrints = printRecordDao.getTotalPrintsByDateRange(startOfDay, endOfDay)
        val recordCount = printRecordDao.getRecordCountByDateRange(startOfDay, endOfDay)
        val averagePerRecord = if (recordCount > 0) totalPrints.toDouble() / recordCount else 0.0
        
        return DailyStatistics(
            date = startOfDay,
            totalPrints = totalPrints,
            recordCount = recordCount,
            averagePerRecord = averagePerRecord
        )
    }
    
    suspend fun getWeeklyStatistics(): List<DailyStatistics> {
        val statistics = mutableListOf<DailyStatistics>()
        for (i in 6 downTo 0) {
            val date = LocalDate.now().minusDays(i.toLong())
            statistics.add(getDailyStatistics(date))
        }
        return statistics
    }
    
    suspend fun getMonthlyStatistics(): List<DailyStatistics> {
        val statistics = mutableListOf<DailyStatistics>()
        val today = LocalDate.now()
        val startOfMonth = today.withDayOfMonth(1)
        var currentDate = startOfMonth
        
        while (currentDate <= today) {
            statistics.add(getDailyStatistics(currentDate))
            currentDate = currentDate.plusDays(1)
        }
        return statistics
    }
    
    suspend fun deleteAllRecords() {
        printRecordDao.deleteAll()
    }
}
