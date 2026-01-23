package com.example.dogday

import kotlinx.coroutines.flow.Flow

class DogRepository(private val dogDao: DogDao) {
    val allTags: Flow<List<Tag>> = dogDao.getAllTags()

    fun getLogsForDate(date: String): Flow<List<DogLog>> = dogDao.getLogsByDate(date)

    suspend fun insertLog(log: DogLog) = dogDao.insertLog(log)

    suspend fun deleteLog(log: DogLog) = dogDao.deleteLog(log)

    suspend fun insertTag(tag: Tag) = dogDao.insertTag(tag)
}