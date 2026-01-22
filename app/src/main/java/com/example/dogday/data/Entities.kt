package com.example.dogday.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// 1. 标签表：存储“洗澡”、“驱虫”等
@Entity(tableName = "tags")
data class Tag(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val colorIndex: Int // 0-9，对应你说的10种颜色
)

// 2. 记录表：存储具体的执行时刻
@Entity(tableName = "records")
data class DogRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tagId: Int,       // 关联标签的ID
    val date: String,    // 日期，格式如 "2026-01-22"
    val time: String     // 时间，格式如 "14:30"
)