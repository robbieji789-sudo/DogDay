package com.example.dogday

import androidx.room.Entity
import androidx.room.PrimaryKey

// 标签表
@Entity(tableName = "tags")
data class Tag(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val colorHex: String,
    val order: Int
)

// 打卡记录表
@Entity(tableName = "records")
data class Record(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tagId: Int,
    val date: String,  // 存储格式: 2026-01-22
    val time: String   // 存储格式: 15:30
)