package com.example.dogday

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

@Composable
fun CalendarMonthGrid(viewModel: DogViewModel) {
    // 1. 从 ViewModel 中观察 42 个日期的列表
    // 注意：确保你已经在 ViewModel 里按我上一条建议写好了 calendarDays
    val days by viewModel.calendarDays.collectAsState()
    val currentMonth by viewModel.currentMonth.collectAsState()

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {

        // 2. 显示星期表头 (一, 二, 三, 四, 五, 六, 日)
        Row(modifier = Modifier.fillMaxWidth()) {
            val weekdays = listOf("一", "二", "三", "四", "五", "六", "日")
            weekdays.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 3. 核心：7列的网格布局
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth(), // 让它填充 Box 容器
            userScrollEnabled = false // 既然是静态网格，禁用内部滑动，防止跟外层冲突
        ) {
            items(days) { date ->
                CalendarDayItem(
                    date = date,
                    isCurrentMonth = date.month == currentMonth.month // 判断是否为本月日期
                )
            }
        }
    }
}

@Composable
fun CalendarDayItem(date: LocalDate, isCurrentMonth: Boolean) {
    Box(
        modifier = Modifier
            .aspectRatio(1f) // 使格子变成正方形
            .padding(2.dp)
            .background(
                if (date == LocalDate.now()) Color(0xFFFFE082) else Color.Transparent, // 今天高亮
                shape = MaterialTheme.shapes.small
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            color = if (isCurrentMonth) Color.Black else Color.LightGray, // 非本月日期显示灰色
            fontSize = 14.sp
        )
    }
}