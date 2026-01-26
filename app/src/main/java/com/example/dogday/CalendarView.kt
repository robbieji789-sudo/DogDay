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
                    // --- 关键修改 2: 判断当前日期是否被选中 ---
                    // 将当前的 LocalDate 转为 String，与状态中的字符串对比
                    val isSelected = date.toString() == selectedDateStr

                    CalendarDayItem(
                        date = date,
                        isCurrentMonth = date.month == monthForPage.month,
                        isSelected = isSelected, // 传递选中状态
                        onDateClick = { clickedDate ->
                            // --- 关键修改 3: 触发点击回调 ---
                            // 调用我们之前在 ViewModel 里准备好的方法
                            viewModel.onDateClick(clickedDate)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarDayItem(
    date: java.time.LocalDate,
    isCurrentMonth: Boolean,
    isSelected: Boolean, // 新增参数：是否被选中
    onDateClick: (java.time.LocalDate) -> Unit // 新增参数：点击回调
) {
    Box(
        modifier = Modifier // 必须从 Modifier 开始
            .clickable { onDateClick(date) } // 然后接 .clickable
            .aspectRatio(1f)
            .padding(4.dp)
            .background(
                color = when {
                    isSelected -> Color(0xFF6200EE)
                    date == java.time.LocalDate.now() -> Color(0xFFFFE082)
                    else -> Color.Transparent
                },
                shape = androidx.compose.foundation.shape.CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            color = when {
                isSelected -> Color.White // 选中时文字变白
                isCurrentMonth -> Color.Black
                else -> Color.LightGray
            },
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}