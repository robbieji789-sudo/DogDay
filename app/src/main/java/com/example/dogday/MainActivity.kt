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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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
        // 顶部标题（显示年份月份，例如：2026年1月）
        val currentMonth by viewModel.currentMonth.collectAsState()

        // 假设你的 TopHeader 已经包含了标题，
        // 或者你可以简单传参给它来动态显示当前月份
        TopHeader()

        // --- 上：日历界面 (正式集成) ---
        // 我们给日历分配约 1.3f 的权重，确保 42 个格子有足够的垂直空间
        Box(
            modifier = Modifier
                .weight(2.8f)
                .fillMaxWidth()
//                .padding(vertical = 8.dp)
        ) {
            CalendarMonthGrid(viewModel = viewModel)
        }

        // --- 在这里插入分隔线 ---
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp), // 左右留点边距更好看
            thickness = 1.dp,                               // 线条粗细
            color = Color.LightGray.copy(alpha = 0.5f)      // 线条颜色，设置半透明显得高级
        )

        // --- 中：任务标签页 ---
        TagSection(
            modifier = Modifier.weight(0.9f),
            viewModel = viewModel
        )

        // --- 在这里插入分隔线 ---
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp), // 左右留点边距更好看
            thickness = 1.dp,                               // 线条粗细
            color = Color.LightGray.copy(alpha = 0.5f)      // 线条颜色，设置半透明显得高级
        )

        // --- 下：今日已完成 ---
        DoneListSection(
            modifier = Modifier.weight(1.0f),
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
    val tags by viewModel.tags.collectAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }

    // --- 新增：用于删除确认弹窗的状态 ---
    var showDeleteDialog by remember { mutableStateOf(false) }
    var tagToDelete by remember { mutableStateOf<Tag?>(null) }

    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = "任务标签 (双击添加，长按删除)",
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
                            onDoubleClick = { viewModel.addLog(tag.id) }, // 双击触发存入数据库
                            onLongClick = {                                 // 修改这里：记录要删除哪个，并打开弹窗
                                tagToDelete = tag
                                showDeleteDialog = true  } // 长按删除
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
            OutlinedButton(onClick = { showDialog = true }) {
                Text("+ 自定义", fontSize = 12.sp)
            }
        }
    }
    // 如果状态为 true，显示弹窗
    if (showDialog) {
        AddTagDialog(
            onDismiss = { showDialog = false },
            onConfirm = { name, color ->
                viewModel.addTag(name, color)
                showDialog = false
            }
        )
    }

    if (showDeleteDialog && tagToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("确认删除标签？") },
            text = { Text("删除标签“${tagToDelete?.name}”将同时清除所有关联的狗狗活动记录，此操作无法撤销。") },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red), // 警告色
                    onClick = {
                        tagToDelete?.let { viewModel.deleteTag(it) }
                        showDeleteDialog = false
                        tagToDelete = null
                    }
                ) { Text("确认删除", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("取消") }
            }
        )
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
        // --- 修改这里的标题逻辑 ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "已完成列表",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )

            // 如果记录不为空，就在后面跟上数字
            if (logs.isNotEmpty()) {
                Spacer(modifier = Modifier.width(8.dp)) // 留一点间距

                // 数字提醒：使用半透明背景或特殊的圆角矩形包装，更显精致
                Box(
                    modifier = Modifier
                        .background(
                            color = Color(0xFF6200EE).copy(alpha = 0.1f), // 淡淡的品牌色背景
                            shape = CircleShape
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = logs.size.toString(),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6200EE) // 品牌紫色数字
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

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

@Composable
fun AddTagDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Int) -> Unit
) {
    var tagName by remember { mutableStateOf("") }
    // 默认选择调色盘中的第一个颜色
    var selectedColorIndex by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("新建任务标签") },
        text = {
            Column {
                TextField(
                    value = tagName,
                    onValueChange = { tagName = it },
                    label = { Text("标签名称（如：遛狗）") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("选择颜色：", fontSize = 14.sp, fontWeight = FontWeight.Bold)

                // 颜色选择器：展示我们预设的 10 种颜色
                FlowRow(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    com.example.dogday.ui.theme.TagColorPalette.forEachIndexed { index, color ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(color, shape = CircleShape)
                                .combinedClickable(
                                    onClick = { selectedColorIndex = index }
                                )
                                .padding(4.dp)
                        ) {
                            if (selectedColorIndex == index) {
                                // 选中的颜色加一个白点标记
                                Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.5f), CircleShape))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (tagName.isNotBlank()) {
                        val colorInt = com.example.dogday.ui.theme.TagColorPalette[selectedColorIndex].toArgb()
                        onConfirm(tagName, colorInt)
                    }
                }
            ) { Text("保存") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}