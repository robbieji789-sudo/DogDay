package com.example.dogday

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "logs")
data class DogLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tagId: Int, // 对应 Tag 的 id
    val date: String, // 存储格式如 "2026-01-23"
    val timestamp: Long // 存储精确的双击时间戳
)