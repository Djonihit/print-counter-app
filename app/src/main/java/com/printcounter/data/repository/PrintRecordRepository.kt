package com.printcounter.data.repository

import com.printcounter.data.dao.PrintRecordDao
import com.printcounter.domain.model.DailyStatistics
import com.printcounter.domain.model.FormatStatistics
import com.printcounter.domain.model.PrintRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneId

class PrintRecordRepository(private val printRecordDao: PrintRecordDao) {
    
    fun getAllRecords(): Flow<List<PrintRecord>> = printRecordDao.getAllRecords()
    
    fun getRecordsByWorkType(workType: String): Flow<List<PrintRecord>> = 
        printRecordDao.getRecordsByWorkType(workType)
    
    fun getRecordsByFormat(workType: String, format: String): Flow<List<PrintRecord>> = 
        printRecordDao.getRecordsByWorkTypeAndFormat(workType, format)
    
    suspend fun addRecord(
        quantity: Int,
        format: String,
        workType: String,
        description: String = "",
        deviceName: String = ""
    ): Long {
        val record = PrintRecord(
            quantity = quantity,
            format = format,
            workType = workType,
            description = description,
            deviceName = deviceName,
            timestamp = System.currentTimeMillis()
        )
        return printRecordDao.insert(record)
    }
    
    suspend fun updateRecord(record: PrintRecord) {
        printRecordDao.update(record)
    }
    
    suspend fun deleteRecord(record: PrintRecord) {
        printRecordDao.delete(record)
    }
    
    fun getTotalQuantity(): Flow<Int?> = printRecordDao.getTotalQuantity()
    
    suspend fun getTotalByWorkType(workType: String): Int =
        printRecordDao.getTotalQuantityByWorkType(workType)
    
    suspend fun getTotalByFormat(workType: String, format: String): Int =
        printRecordDao.getTotalQuantityByFormat(workType, format)
    
    suspend fun getDailyStatistics(date: LocalDate): DailyStatistics {
        val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1
        
        val totalPrints = printRecordDao.getTotalQuantityByDateRange("PRINT", startOfDay, endOfDay)
        val totalEmbroidery = printRecordDao.getTotalQuantityByDateRange("EMBROIDERY", startOfDay, endOfDay)
        val printRecordCount = printRecordDao.getRecordCountByDateRange("PRINT", startOfDay, endOfDay)
        val embroideryRecordCount = printRecordDao.getRecordCountByDateRange("EMBROIDERY", startOfDay, endOfDay)
        
        return DailyStatistics(
            date = startOfDay,
            totalPrints = totalPrints,
            totalEmbroidery = totalEmbroidery,
            printRecordCount = printRecordCount,
            embroideryRecordCount = embroideryRecordCount
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
