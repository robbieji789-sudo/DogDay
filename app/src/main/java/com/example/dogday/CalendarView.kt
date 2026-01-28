package com.example.dogday

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable // <--- 就是这一行！
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape // <--- 确保也有这个，用于画圆
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

@Composable
fun CalendarMonthGrid(viewModel: DogViewModel) {
    val pagerState = rememberPagerState(initialPage = viewModel.initialPage, pageCount = { 1000 })
    val currentMonth by viewModel.currentMonth.collectAsState()

    // --- 关键修改 1: 获取当前选中的日期状态 ---
    // selectedDate 在 ViewModel 里是 String 类型 (如 "2026-03-31")
    val selectedDateStr by viewModel.selectedDate.collectAsState()

    val markings by viewModel.calendarMarkings.collectAsState()

    // 监听滑动，更新标题
    LaunchedEffect(pagerState.currentPage) {
        viewModel.onMonthChange(viewModel.getYearMonthForPage(pagerState.currentPage))
    }

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        // 1. 年份月份标题
        Text(
            text = "${currentMonth.year}年${currentMonth.monthValue}月",
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        // 2. 星期表头
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
            listOf("一", "二", "三", "四", "五", "六", "日").forEach {
                Text(
                    text = it,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // 3. 左右滑动容器
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) { page ->
            val monthForPage = viewModel.getYearMonthForPage(page)
            val days = viewModel.getDaysInMonthPage(monthForPage)

            // 4. 日期网格
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(350.dp),
                userScrollEnabled = false
            ) {
                items(days) { date ->
                    val dateStr = date.toString()
                    val dayColors = markings[dateStr] ?: emptyList() // 获取当天的颜色列表

                    CalendarDayItem(
                        date = date,
                        isCurrentMonth = date.month == monthForPage.month,
                        isSelected = date.toString() == selectedDateStr,
                        taskColors = dayColors, // 将颜色列表传进去
                        onDateClick = { viewModel.onDateClick(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarDayItem(
    date: LocalDate,
    isCurrentMonth: Boolean,
    isSelected: Boolean,
    taskColors: List<Int>, // 新增：这一天的任务颜色列表
    onDateClick: (LocalDate) -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(
                color = when {
                    isSelected -> Color(0xFF6200EE).copy(alpha = 0.15f) // 选中时浅色背景
                    date == LocalDate.now() -> Color(0xFFFFE082).copy(alpha = 0.5f)
                    else -> Color.Transparent
                }
            )
            .clickable { onDateClick(date) },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // 1. 日期数字
            Text(
                text = date.dayOfMonth.toString(),
                color = when {
                    isSelected -> Color(0xFF6200EE)
                    isCurrentMonth -> Color.Black
                    else -> Color.LightGray
                },
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )

            // 2. 下方的小圆圈或数字标记
            if (taskColors.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(top = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (taskColors.size <= 3) {
                        // 任务 <= 3：显示所有圆圈
                        taskColors.forEach { colorInt ->
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(Color(colorInt), shape = CircleShape)
                            )
                        }
                    } else {
                        // 任务 > 3：显示前 3 个 + 剩余数量
                        taskColors.take(3).forEach { colorInt ->
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(Color(colorInt), shape = CircleShape)
                            )
                        }
                        Text(
                            text = "+${taskColors.size - 3}",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}