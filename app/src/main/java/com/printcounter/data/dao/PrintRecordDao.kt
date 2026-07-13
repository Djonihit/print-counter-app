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
    
    @Query("SELECT * FROM print_records WHERE workType = :workType ORDER BY timestamp DESC")
    fun getRecordsByWorkType(workType: String): Flow<List<PrintRecord>>
    
    @Query("SELECT * FROM print_records WHERE workType = :workType AND format = :format ORDER BY timestamp DESC")
    fun getRecordsByWorkTypeAndFormat(workType: String, format: String): Flow<List<PrintRecord>>
    
    @Query("SELECT SUM(quantity) FROM print_records")
    fun getTotalQuantity(): Flow<Int?>
    
    @Query("SELECT SUM(quantity) FROM print_records WHERE workType = :workType")
    suspend fun getTotalQuantityByWorkType(workType: String): Int
    
    @Query("SELECT SUM(quantity) FROM print_records WHERE workType = :workType AND format = :format")
    suspend fun getTotalQuantityByFormat(workType: String, format: String): Int
    
    @Query("SELECT SUM(quantity) FROM print_records WHERE workType = :workType AND timestamp BETWEEN :startTime AND :endTime")
    suspend fun getTotalQuantityByDateRange(workType: String, startTime: Long, endTime: Long): Int
    
    @Query("SELECT COUNT(*) FROM print_records WHERE workType = :workType AND timestamp BETWEEN :startTime AND :endTime")
    suspend fun getRecordCountByDateRange(workType: String, startTime: Long, endTime: Long): Int
    
    @Query("SELECT COUNT(*) FROM print_records WHERE workType = :workType AND format = :format")
    suspend fun getRecordCountByFormat(workType: String, format: String): Int
    
    @Query("DELETE FROM print_records")
    suspend fun deleteAll()
    
    @Query("DELETE FROM print_records WHERE id = :id")
    suspend fun deleteById(id: Int)
}
