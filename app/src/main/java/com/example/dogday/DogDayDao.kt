package com.example.dogday

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DogDayDao {
    // 获取所有标签，按顺序排列
    @Query("SELECT * FROM tags ORDER BY `order` ASC")
    fun getAllTags(): Flow<List<Tag>>

    // 插入新标签
    @Insert
    suspend fun insertTag(tag: Tag)

    // 删除标签
    @Delete
    suspend fun deleteTag(tag: Tag)

    // 根据日期获取当天的所有记录
    @Query("SELECT * FROM records WHERE date = :date")
    fun getRecordsByDate(date: String): Flow<List<Record>>

    // 插入一条打卡记录
    @Insert
    suspend fun insertRecord(record: Record)

    // 删除一条打卡记录
    @Delete
    suspend fun deleteRecord(record: Record)
}