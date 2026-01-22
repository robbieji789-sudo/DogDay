package com.example.dogday.ui.theme

import androidx.compose.ui.graphics.Color

// 定义你喜欢的 10 种颜色，顺序固定
val TagColorPalette = listOf(
    Color(0xFFF44336), // 0: 红色
    Color(0xFF4CAF50), // 1: 绿色
    Color(0xFF2196F3), // 2: 蓝色
    Color(0xFFFFEB3B), // 3: 黄色
    Color(0xFFFF9800), // 4: 橙色
    Color(0xFF9C27B0), // 5: 紫色
    Color(0xFF00BCD4), // 6: 青色
    Color(0xFFE91E63), // 7: 粉色
    Color(0xFF795548), // 8: 棕色
    Color(0xFF607D8B)  // 9: 蓝灰色
)

// 辅助函数：根据索引安全获取颜色
fun getTagColor(index: Int): Color {
    return TagColorPalette.getOrElse(index % TagColorPalette.size) { Color.Gray }
}