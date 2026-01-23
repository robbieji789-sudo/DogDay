package com.example.dogday

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Tag::class, DogLog::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dogDao(): DogDao
}