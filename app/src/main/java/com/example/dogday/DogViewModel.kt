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

    // --- 原有逻辑保持不变 ---
    private val _selectedDate = MutableStateFlow(LocalDate.now().toString())
    val selectedDate: StateFlow<String> = _selectedDate

    val tags = repository.allTags

    fun getLogsForSelectedDate(date: String) = repository.getLogsForDate(date)

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

    fun addTag(name: String, color: Int) {
        viewModelScope.launch {
            val newTag = Tag(name = name, color = color, orderIndex = (System.currentTimeMillis() / 1000).toInt())
            repository.insertTag(newTag)
        }
    }

    fun deleteTag(tag: Tag) {
        viewModelScope.launch {
            repository.deleteTag(tag)
        }
    }

    // ================= 重点修改部分：适配滑动日历 =================

    // 1. 定义初始页码。我们设为 500，这样用户往左滑(过去)和往右滑(未来)都有很大空间。
    val initialPage = 500

    // 2. 当前显示的月份（由 UI 的滑动页面索引决定）
    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth

    /**
     * 提供给 UI 调用：根据 Pager 的当前页码计算对应的 YearMonth
     * 计算公式：当前月份 = 现实中的本月 + (页面索引 - 初始页码)
     */
    fun getYearMonthForPage(page: Int): YearMonth {
        val offset = (page - initialPage).toLong()
        return YearMonth.now().plusMonths(offset)
    }

    /**
     * 提供给 UI 调用：更新当前的月份状态（用于在顶部显示 2025年12月）
     */
    fun onMonthChange(newMonth: YearMonth) {
        _currentMonth.value = newMonth
    }

    /**
     * 提供给 UI 调用：根据具体的月份获取 42 个格子数据
     * 注意：这里不再是全应用唯一的列表，而是根据传入的月份动态计算
     */
    fun getDaysInMonthPage(month: YearMonth): List<LocalDate> {
        return CalendarHelper.getDaysInMonthPage(month)
    }

    // 这一段是你之前代码里的，滑动模式下我们更倾向于使用上面的 getDaysInMonthPage 函数
    // 但保留它也不会报错，可以留着兼容旧的静态视图
    val calendarDays: StateFlow<List<LocalDate>> = _currentMonth
        .map { month -> CalendarHelper.getDaysInMonthPage(month) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}