package com.printcounter

import android.app.Application
import com.printcounter.data.database.PrintCounterDatabase
import com.printcounter.data.repository.PrintRecordRepository

class PrintCounterApplication : Application() {
    
    val database: PrintCounterDatabase by lazy { PrintCounterDatabase.getDatabase(this) }
    val repository: PrintRecordRepository by lazy {
        PrintRecordRepository(database.printRecordDao())
    }
}
