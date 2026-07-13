package com.printcounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.printcounter.data.database.PrintCounterDatabase
import com.printcounter.data.repository.PrintRecordRepository
import com.printcounter.ui.screens.CounterScreen
import com.printcounter.ui.screens.HistoryScreen
import com.printcounter.ui.screens.StatisticsScreen
import com.printcounter.ui.theme.PrintCounterAppTheme
import com.printcounter.ui.viewmodel.PrintCounterViewModel
import com.printcounter.ui.viewmodel.PrintCounterViewModelFactory

class MainActivity : ComponentActivity() {
    
    private lateinit var viewModel: PrintCounterViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize database and repository
        val database = PrintCounterDatabase.getDatabase(this)
        val repository = PrintRecordRepository(database.printRecordDao())
        
        // Initialize ViewModel
        viewModel = ViewModelProvider(
            this,
            PrintCounterViewModelFactory(repository)
        ).get(PrintCounterViewModel::class.java)
        
        setContent {
            PrintCounterAppTheme {
                MainApp(viewModel)
            }
        }
    }
}

@Composable
fun MainApp(
    viewModel: PrintCounterViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val uiState by remember { viewModel.uiState }
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Counter") },
                    label = { Text("Счётчик") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Info, contentDescription = "Statistics") },
                    label = { Text("Статистика") },
                    selected = selectedTab == 1,
                    onClick = { 
                        selectedTab = 1
                        viewModel.loadWeeklyStatistics()
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Settings, contentDescription = "History") },
                    label = { Text("История") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> CounterScreen(
                onAddPrint = { quantity, format ->
                    viewModel.addPrintRecord(quantity, format)
                },
                onAddEmbroidery = { quantity, format ->
                    viewModel.addEmbroideryRecord(quantity, format)
                },
                modifier = Modifier.padding(innerPadding)
            )
            1 -> StatisticsScreen(
                totalPrints = uiState.totalPrints,
                totalEmbroidery = uiState.totalEmbroidery,
                dailyStatistics = uiState.dailyStatistics,
                weeklyStatistics = uiState.weeklyStatistics,
                onLoadWeeklyStats = { viewModel.loadWeeklyStatistics() },
                modifier = Modifier.padding(innerPadding)
            )
            2 -> HistoryScreen(
                records = uiState.allRecords,
                onDeleteRecord = { record ->
                    viewModel.deleteRecord(record)
                },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
