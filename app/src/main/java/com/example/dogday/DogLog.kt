package com.example.dogday

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey // 必须导入这个

@Entity(
    tableName = "logs",
    foreignKeys = [
        ForeignKey(
            entity = Tag::class,           // 关联的父表是 Tag
            parentColumns = ["id"],        // Tag 表中对应的列是 id
            childColumns = ["tagId"],      // DogLog 表中对应的列是 tagId
            onDelete = ForeignKey.CASCADE  // 关键：开启级联删除
        )
    ]
)
data class DogLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tagId: Long,
    val date: String,
    val timestamp: Long = System.currentTimeMillis()
)