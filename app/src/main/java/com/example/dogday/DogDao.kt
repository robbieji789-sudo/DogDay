package com.example.dogday

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DogDao {
    // 获取所有标签，按顺序排
    @Query("SELECT * FROM tags ORDER BY orderIndex ASC")
    fun getAllTags(): Flow<List<Tag>>

    // 获取某天的所有记录
    @Query("SELECT * FROM logs WHERE date = :selectedDate")
    fun getLogsByDate(selectedDate: String): Flow<List<DogLog>>

    @Insert
    suspend fun insertLog(log: DogLog)

    @Delete
    suspend fun deleteLog(log: DogLog)

    @Insert
    suspend fun insertTag(tag: Tag)

    @Delete
    suspend fun deleteTag(tag: Tag)
}