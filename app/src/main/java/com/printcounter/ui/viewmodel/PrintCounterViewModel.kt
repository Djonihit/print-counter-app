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
    val records: List<PrintRecord> = emptyList(),
    val totalPrints: Int = 0,
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
                // Collect flows
                repository.getAllRecords().collect { records ->
                    _uiState.value = _uiState.value.copy(records = records)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Unknown error")
            }
        }
        
        viewModelScope.launch {
            try {
                repository.getTotalPrints().collect { total ->
                    _uiState.value = _uiState.value.copy(totalPrints = total)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Unknown error")
            }
        }
    }
    
    fun addPrintRecord(quantity: Int, description: String = "", deviceName: String = "") {
        viewModelScope.launch {
            try {
                repository.addPrintRecord(quantity, description, deviceName)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Failed to add record")
            }
        }
    }
    
    fun deletePrintRecord(record: PrintRecord) {
        viewModelScope.launch {
            try {
                repository.deletePrintRecord(record)
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
