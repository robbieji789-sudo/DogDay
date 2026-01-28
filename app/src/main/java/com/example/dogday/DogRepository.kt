package com.example.dogday

import kotlinx.coroutines.flow.Flow

class DogRepository(private val dogDao: DogDao) {
    val allTags: Flow<List<Tag>> = dogDao.getAllTags()

    val allLogs: Flow<List<DogLog>> = dogDao.getAllLogs()

    fun getLogsForDate(date: String): Flow<List<DogLog>> = dogDao.getLogsByDate(date)

    suspend fun insertLog(log: DogLog) = dogDao.insertLog(log)

    suspend fun deleteLog(log: DogLog) = dogDao.deleteLog(log)

    suspend fun insertTag(tag: Tag) = dogDao.insertTag(tag)

    suspend fun deleteTag(tag: Tag) = dogDao.deleteTag(tag) // 补充这一行
}