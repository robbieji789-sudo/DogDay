package com.example.dogday

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class DogViewModel(private val repository: DogRepository) : ViewModel() {

    // 当前选中的日期，默认是今天
    private val _selectedDate = MutableStateFlow(LocalDate.now().toString())
    val selectedDate: StateFlow<String> = _selectedDate

    // 获取当前选中日期的记录
    // 这里的逻辑是：当日标改变时，UI 会自动观察到变化
    val tags = repository.allTags

    fun getLogsForSelectedDate(date: String) = repository.getLogsForDate(date)

    // 执行添加记录的操作（对应你的双击动作）
    fun addLog(tagId: Long) {
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



    // 这里的 repository.insertTag 已经在你代码中了
    fun addTag(name: String, color: Int) {
        viewModelScope.launch {
            // 这里的 orderIndex 暂时用时间戳简单代替，保证排序
            val newTag = Tag(name = name, color = color, orderIndex = (System.currentTimeMillis() / 1000).toInt())
            repository.insertTag(newTag)
        }
    }

    fun deleteTag(tag: Tag) {
        viewModelScope.launch {
            // 注意：实际开发中删除标签通常要考虑级联删除已完成的任务，目前我们先做简单的删除
            repository.deleteTag(tag)
        }
    }

    // 当前选中的月份
    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth

    // 核心：使用 combine 或 map，当月份变化时，自动重新计算 42 个日期
    val calendarDays: StateFlow<List<LocalDate>> = _currentMonth
        .map { month -> CalendarHelper.getDaysInMonthPage(month) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 切换月份的方法
    fun onMonthChange(newMonth: YearMonth) {
        _currentMonth.value = newMonth
    }
}