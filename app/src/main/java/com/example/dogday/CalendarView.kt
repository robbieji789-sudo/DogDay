package com.example.dogday

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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

    // 监听滑动，更新标题
    LaunchedEffect(pagerState.currentPage) {
        viewModel.onMonthChange(viewModel.getYearMonthForPage(pagerState.currentPage))
    }

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        // 1. 年份月份标题 (2026年3月)
        Text(
            text = "${currentMonth.year}年${currentMonth.monthValue}月",
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        // 2. 星期表头 (这一行是独立的 Row，不占网格的坑位)
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
            val days = viewModel.getDaysInMonthPage(monthForPage) // 这里必须确保返回 42 天

            // 4. 日期网格 (专门显示 42 个日期)
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                // 增加高度到 320dp，确保 6 行日期都能露出来
                modifier = Modifier.height(350.dp),
                userScrollEnabled = false
            ) {
                items(days) { date ->
                    CalendarDayItem(
                        date = date,
                        isCurrentMonth = date.month == monthForPage.month
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarDayItem(date: LocalDate, isCurrentMonth: Boolean) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .background(
                if (date == LocalDate.now()) Color(0xFFFFE082) else Color.Transparent,
                shape = MaterialTheme.shapes.small
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            color = if (isCurrentMonth) Color.Black else Color.LightGray,
            fontSize = 14.sp
        )
    }
}