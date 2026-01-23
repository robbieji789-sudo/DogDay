package com.example.dogday

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class DogViewModel(private val repository: DogRepository) : ViewModel() {

    // 当前选中的日期，默认是今天
    private val _selectedDate = MutableStateFlow(LocalDate.now().toString())
    val selectedDate: StateFlow<String> = _selectedDate

    // 获取当前选中日期的记录
    // 这里的逻辑是：当日标改变时，UI 会自动观察到变化
    val tags = repository.allTags

    fun getLogsForSelectedDate(date: String) = repository.getLogsForDate(date)

    // 执行添加记录的操作（对应你的双击动作）
    fun addLog(tagId: Int) {
        viewModelScope.launch {
            val newLog = DogLog(
                tagId = tagId,
                date = _selectedDate.value,
                timestamp = System.currentTimeMillis()
            )
            repository.insertLog(newLog)
        }
    }

    fun deleteLog(log: DogLog) {
        viewModelScope.launch {
            repository.deleteLog(log)
        }
    }

    fun onDateSelected(date: String) {
        _selectedDate.value = date
    }
}