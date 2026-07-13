package com.printcounter.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.printcounter.domain.model.PrintRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface PrintRecordDao {
    
    @Insert
    suspend fun insert(record: PrintRecord): Long
    
    @Update
    suspend fun update(record: PrintRecord)
    
    @Delete
    suspend fun delete(record: PrintRecord)
    
    @Query("SELECT * FROM print_records WHERE id = :id")
    suspend fun getById(id: Int): PrintRecord?
    
    @Query("SELECT * FROM print_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<PrintRecord>>
    
    @Query("SELECT * FROM print_records ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentRecords(limit: Int): Flow<List<PrintRecord>>
    
    @Query("SELECT * FROM print_records WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun getRecordsByDateRange(startTime: Long, endTime: Long): Flow<List<PrintRecord>>
    
    @Query("SELECT SUM(quantity) FROM print_records")
    fun getTotalPrints(): Flow<Int>
    
    @Query("SELECT SUM(quantity) FROM print_records WHERE timestamp BETWEEN :startTime AND :endTime")
    suspend fun getTotalPrintsByDateRange(startTime: Long, endTime: Long): Int
    
    @Query("SELECT COUNT(*) FROM print_records WHERE timestamp BETWEEN :startTime AND :endTime")
    suspend fun getRecordCountByDateRange(startTime: Long, endTime: Long): Int
    
    @Query("DELETE FROM print_records")
    suspend fun deleteAll()
}
