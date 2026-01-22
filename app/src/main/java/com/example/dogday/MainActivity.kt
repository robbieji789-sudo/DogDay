package com.example.dogday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dogday.ui.theme.DogDayTheme
import com.example.dogday.ui.theme.TagColorPalette

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 允许内容延伸到状态栏下方，配合 statusBarsPadding 使用
        // WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            DogDayTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DogDayMainScreen()
                }
            }
        }
    }
}

@Composable
fun DogDayMainScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部标题模块
        TopHeader()

        // --- 上：日历界面 ---
        Box(
            modifier = Modifier
                .weight(1.2f)
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5)), // 极浅的灰色背景
            contentAlignment = Alignment.Center
        ) {
            Text("📅 日历月视图预留", fontSize = 16.sp, color = Color.Gray)
        }

        // --- 中：任务标签页 ---
        TagSection(modifier = Modifier.weight(0.9f))

        Divider(thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.5f))

        // --- 下：今日已完成 ---
        DoneListSection(modifier = Modifier.weight(1.1f))
    }
}

@Composable
fun TopHeader() {
    // 整个标题栏的容器
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF9C27B0)) // 设置紫色背景 (Deep Purple)
            .statusBarsPadding()           // 避开状态栏和摄像头
            .padding(vertical = 16.dp),    // 增加上下间距
        horizontalAlignment = Alignment.CenterHorizontally, // 子元素水平居中
        verticalArrangement = Arrangement.Center           // 子元素垂直居中
    ) {
        Text(
            text = "DogDay",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold, // 加粗
            color = Color.White               // 白色字体
        )
        Text(
            text = "Every dog has its day",
            fontSize = 12.sp,
            fontStyle = FontStyle.Italic,
            color = Color.White.copy(alpha = 0.8f) // 略带透明度的白色，增加层次感
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TagSection(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = "任务标签 (双击添加)",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // 这里后续可以使用 FlowRow，现在先用 Row 演示
        Row(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
            val demoTags = listOf("洗澡", "驱虫")
            demoTags.forEachIndexed { index, name ->
                Surface(
                    modifier = Modifier
                        .padding(4.dp)
                        .combinedClickable(
                            onClick = { },
                            onDoubleClick = { println("已双击: $name") }
                        ),
                    color = TagColorPalette[index % TagColorPalette.size],
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = name,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = Color.White
                    )
                }
            }

            OutlinedButton(
                onClick = { /* TODO */ },
                modifier = Modifier.padding(4.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Text("+ 自定义", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun DoneListSection(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = "今日已完成",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn {
            items(3) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("洗澡", fontWeight = FontWeight.Medium)
                    Text("14:30", color = Color.Gray, fontSize = 14.sp)
                }
                Divider(thickness = 0.5.dp, color = Color.White.copy(alpha = 0.1f))
            }
        }
    }
}