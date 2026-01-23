package com.example.dogday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.dogday.ui.theme.DogDayTheme
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 使用我们定义的 DatabaseProvider 拿数据库实例
        val db = DatabaseProvider.getDatabase(applicationContext)
        val repository = DogRepository(db.dogDao())

        val viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DogViewModel(repository) as T
            }
        })[DogViewModel::class.java]

        setContent {
            DogDayTheme {
                DogDayMainScreen(viewModel)
            }
        }
    }
}

@Composable
fun DogDayMainScreen(viewModel: DogViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopHeader()

        // --- 上：日历界面 (暂存占位) ---
        Box(
            modifier = Modifier
                .weight(1.2f)
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Text("📅 日历月视图预留\n(后续集成装饰器)", fontSize = 16.sp, color = Color.Gray)
        }

        // --- 中：任务标签页 ---
        TagSection(
            modifier = Modifier.weight(0.9f),
            viewModel = viewModel
        )

        HorizontalDivider(thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.5f))

        // --- 下：今日已完成 ---
        DoneListSection(
            modifier = Modifier.weight(1.1f),
            viewModel = viewModel
        )
    }
}

@Composable
fun TopHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF9C27B0))
            .statusBarsPadding()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "DogDay", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
        Text(
            text = "Every dog has its day",
            fontSize = 12.sp,
            fontStyle = FontStyle.Italic,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun TagSection(modifier: Modifier = Modifier, viewModel: DogViewModel) {
    // 观察 ViewModel 中的标签列表
    val tags by viewModel.tags.collectAsState(initial = emptyList())

    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = "任务标签 (双击添加)",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // 使用 FlowRow 自动换行显示标签
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tags.forEach { tag ->
                Surface(
                    modifier = Modifier
                        .combinedClickable(
                            onClick = { /* 单击可以选择日期，此处暂不处理 */ },
                            onDoubleClick = { viewModel.addLog(tag.id) } // 双击触发存入数据库
                        ),
                    color = Color(tag.color.toLong() and 0xffffffffL), // 使用数据库存的颜色值
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = tag.name,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = Color.White
                    )
                }
            }

            // 添加按钮
            OutlinedButton(
                onClick = { /* TODO: 弹出对话框输入新标签名 */ },
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Text("+ 自定义", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun DoneListSection(modifier: Modifier = Modifier, viewModel: DogViewModel) {
    // 观察当前选中的日期
    val selectedDate by viewModel.selectedDate.collectAsState()
    // 根据日期观察记录列表
    val logs by viewModel.getLogsForSelectedDate(selectedDate).collectAsState(initial = emptyList())
    // 为了显示标签名，我们需要拿到所有标签做映射
    val tags by viewModel.tags.collectAsState(initial = emptyList())

    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = "今日已完成",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn {
            items(logs) { log ->
                val tagName = tags.find { it.id == log.tagId }?.name ?: "未知任务"
                val timeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(log.timestamp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(tagName, fontWeight = FontWeight.Medium)
                    Text(timeString, color = Color.Gray, fontSize = 14.sp)
                }
                HorizontalDivider(thickness = 0.5.dp, color = Color.Gray.copy(alpha = 0.2f))
            }
        }
    }
}