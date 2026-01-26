package com.example.dogday

import java.time.LocalDate
import java.time.YearMonth
import java.time.DayOfWeek
import java.time.temporal.TemporalAdjusters

object CalendarHelper {
    fun getDaysInMonthPage(month: YearMonth): List<LocalDate> {
        val days = mutableListOf<LocalDate>()

        // 1. 这个月的第一天
        val firstDayOfMonth = month.atDay(1)
        // 2. 找到第一天是周几 (注意：Java 的星期是从周一=1 到 周日=7)
        val dayOfWeek = firstDayOfMonth.dayOfWeek.value

        // 3. 计算前面要补多少天上个月的日期
        // 如果 1 号是周一(1)，补 0 天；如果 1 号是周日(7)，补 6 天
        val daysBefore = dayOfWeek - 1
        val startDate = firstDayOfMonth.minusDays(daysBefore.toLong())

        // 4. 从起始日期开始，连续取 42 天
        for (i in 0 until 42) {
            days.add(startDate.plusDays(i.toLong()))
        }

        return days
    }
}