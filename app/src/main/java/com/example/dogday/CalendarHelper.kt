package com.example.dogday

import java.time.LocalDate
import java.time.YearMonth
import java.time.DayOfWeek
import java.time.temporal.TemporalAdjusters

object CalendarHelper {

    /**
     * 根据给定的年份和月份，计算出填满 7x6 网格所需的 42 个日期
     */
    fun getDaysInMonthPage(yearMonth: YearMonth): List<LocalDate> {
        val days = mutableListOf<LocalDate>()

        // 1. 获取这个月的第一天 (例如: 2026-01-01)
        val firstDayOfMonth = yearMonth.atDay(1)

        // 2. 计算第一天是星期几 (1=周一, ..., 7=周日)
        // 假设我们的日历从周一（Monday）开始
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value

        // 3. 计算需要补多少个“上个月”的天数
        // 如果第一天是周一(1)，偏移量为0；如果是周二(2)，偏移量为1，以此类推
        val daysBefore = firstDayOfWeek - 1

        // 4. 找到 42 个格子的起点（可能是上个月的某一天）
        val startDate = firstDayOfMonth.minusDays(daysBefore.toLong())

        // 5. 从起点开始，连续取 42 天
        for (i in 0 until 42) {
            days.add(startDate.plusDays(i.toLong()))
        }

        return days
    }
}