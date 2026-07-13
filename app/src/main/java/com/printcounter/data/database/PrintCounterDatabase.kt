package com.printcounter.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.printcounter.data.dao.PrintRecordDao
import com.printcounter.domain.model.PrintRecord

@Database(
    entities = [PrintRecord::class],
    version = 1,
    exportSchema = false
)
abstract class PrintCounterDatabase : RoomDatabase() {
    abstract fun printRecordDao(): PrintRecordDao
    
    companion object {
        @Volatile
        private var INSTANCE: PrintCounterDatabase? = null
        
        fun getDatabase(context: Context): PrintCounterDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PrintCounterDatabase::class.java,
                    "print_counter_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
