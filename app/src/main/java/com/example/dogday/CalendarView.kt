package com.example.dogday

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
    val selectedDateStr by viewModel.selectedDate.collectAsState()
    val markings by viewModel.calendarMarkings.collectAsState()

    LaunchedEffect(pagerState.currentPage) {
        viewModel.onMonthChange(viewModel.getYearMonthForPage(pagerState.currentPage))
    }

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        // 1. 年份月份标题 - 这里的文字稍微大一点，不用背景，直接浮在大背景上
        Text(
            text = "${currentMonth.year}年${currentMonth.monthValue}月",
            modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
            textAlign = TextAlign.Center,
            color = Color(0xFF6200EE), // 使用深紫色文字
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
        )

        // 2. 星期表头
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF3E5F5), shape = RoundedCornerShape(4.dp)) // 增加浅灰色底色
                .padding(vertical = 8.dp)
        ) {
            listOf("一", "二", "三", "四", "五", "六", "日").forEachIndexed { index, weekDay ->
                Text(
                    text = weekDay,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    // 周六周日可以用不同的颜色突出
                    color = if (index >= 5) Color(0xFF6200EE) else Color.DarkGray,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.ExtraBold, // 极粗字体
                        fontSize = 14.sp
                    )
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) { page ->
            val monthForPage = viewModel.getYearMonthForPage(page)
            val days = viewModel.getDaysInMonthPage(monthForPage)

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(350.dp),
                userScrollEnabled = false
            ) {
                items(days) { date ->
                    val dateStr = date.toString()
                    val dayColors = markings[dateStr] ?: emptyList()

                    CalendarDayItem(
                        date = date,
                        isCurrentMonth = date.month == monthForPage.month,
                        isSelected = date.toString() == selectedDateStr,
                        taskColors = dayColors,
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
    taskColors: List<Int>,
    onDateClick: (LocalDate) -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(1.dp)
            .clip(CircleShape)
            .background(
                color = when {
                    isSelected -> Color(0xFF6200EE).copy(alpha = 0.15f)
                    date == LocalDate.now() -> Color(0xFFFFE082).copy(alpha = 0.5f)
                    else -> Color.Transparent
                }
            )
            .clickable { onDateClick(date) },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            // 使用 SpaceEvenly 确保日期和圆点区域在垂直方向分布均匀，不拥挤
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxSize().padding(vertical = 2.dp)
        ) {
            // 1. 日期数字
            Text(
                text = date.dayOfMonth.toString(),
                color = when {
                    isSelected -> Color(0xFF6200EE)
                    isCurrentMonth -> Color.Black
                    else -> Color.LightGray
                },
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )

            // 2. 标记区域：只显示前 3 个圆圈，不显示任何数字
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp), // 保持固定高度，防止日期跳动
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 使用 take(3) 获取前三个颜色，如果没有任务则不执行
                taskColors.take(3).forEach { colorInt ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 1.dp)
                            .size(5.dp)
                            .background(Color(colorInt), shape = CircleShape)
                    )
                }
            }
        }
    }
}