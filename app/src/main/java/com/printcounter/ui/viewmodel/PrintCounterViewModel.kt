package com.printcounter.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.printcounter.data.repository.PrintRecordRepository
import com.printcounter.domain.model.DailyStatistics
import com.printcounter.domain.model.PrintRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class PrintCounterUiState(
    val allRecords: List<PrintRecord> = emptyList(),
    val printRecords: List<PrintRecord> = emptyList(),
    val embroideryRecords: List<PrintRecord> = emptyList(),
    val totalQuantity: Int = 0,
    val totalPrints: Int = 0,
    val totalEmbroidery: Int = 0,
    val dailyStatistics: DailyStatistics? = null,
    val weeklyStatistics: List<DailyStatistics> = emptyList(),
    val monthlyStatistics: List<DailyStatistics> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class PrintCounterViewModel(private val repository: PrintRecordRepository) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PrintCounterUiState())
    val uiState: StateFlow<PrintCounterUiState> = _uiState.asStateFlow()
    
    init {
        loadAllData()
    }
    
    private fun loadAllData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                repository.getAllRecords().collect { records ->
                    _uiState.value = _uiState.value.copy(allRecords = records)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Unknown error")
            }
        }
        
        viewModelScope.launch {
            try {
                repository.getRecordsByWorkType("PRINT").collect { records ->
                    _uiState.value = _uiState.value.copy(printRecords = records)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Unknown error")
            }
        }
        
        viewModelScope.launch {
            try {
                repository.getRecordsByWorkType("EMBROIDERY").collect { records ->
                    _uiState.value = _uiState.value.copy(embroideryRecords = records)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Unknown error")
            }
        }
        
        viewModelScope.launch {
            try {
                repository.getTotalQuantity().collect { total ->
                    _uiState.value = _uiState.value.copy(totalQuantity = total ?: 0)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Unknown error")
            }
        }
        
        loadTotals()
        loadDailyStatistics(LocalDate.now())
        loadWeeklyStatistics()
    }
    
    private fun loadTotals() {
        viewModelScope.launch {
            try {
                val printTotal = repository.getTotalByWorkType("PRINT")
                val embroideryTotal = repository.getTotalByWorkType("EMBROIDERY")
                _uiState.value = _uiState.value.copy(
                    totalPrints = printTotal,
                    totalEmbroidery = embroideryTotal
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Failed to load totals")
            }
        }
    }
    
    fun addPrintRecord(quantity: Int, format: String, description: String = "") {
        viewModelScope.launch {
            try {
                repository.addRecord(quantity, format, "PRINT", description)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Failed to add record")
            }
        }
    }
    
    fun addEmbroideryRecord(quantity: Int, format: String, description: String = "") {
        viewModelScope.launch {
            try {
                repository.addRecord(quantity, format, "EMBROIDERY", description)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Failed to add record")
            }
        }
    }
    
    fun deleteRecord(record: PrintRecord) {
        viewModelScope.launch {
            try {
                repository.deleteRecord(record)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Failed to delete record")
            }
        }
    }
    
    fun loadDailyStatistics(date: LocalDate) {
        viewModelScope.launch {
            try {
                val stats = repository.getDailyStatistics(date)
                _uiState.value = _uiState.value.copy(dailyStatistics = stats)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Failed to load statistics")
            }
        }
    }
    
    fun loadWeeklyStatistics() {
        viewModelScope.launch {
            try {
                val stats = repository.getWeeklyStatistics()
                _uiState.value = _uiState.value.copy(weeklyStatistics = stats)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Failed to load statistics")
            }
        }
    }
    
    fun loadMonthlyStatistics() {
        viewModelScope.launch {
            try {
                val stats = repository.getMonthlyStatistics()
                _uiState.value = _uiState.value.copy(monthlyStatistics = stats)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Failed to load statistics")
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

class PrintCounterViewModelFactory(private val repository: PrintRecordRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PrintCounterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PrintCounterViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
