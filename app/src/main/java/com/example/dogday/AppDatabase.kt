package com.example.dogday

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Tag::class, DogLog::class], version = 2, exportSchema = false) // 修改 version 为 2
abstract class AppDatabase : RoomDatabase() {
    abstract fun dogDao(): DogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dogday_db"
                )
                    .fallbackToDestructiveMigration() // 新增这一行：允许破坏性迁移
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}