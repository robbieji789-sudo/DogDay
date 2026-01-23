package com.example.dogday

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tags")
data class Tag(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // 确保这里是 Long
    val name: String,
    val color: Int,      // 使用 Int 存储颜色，如 0xFF4A90E2.toInt()
    val orderIndex: Int
)