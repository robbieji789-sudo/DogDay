package com.example.dogday

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DatabaseProvider {
    @Volatile
    private var instance: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return instance ?: synchronized(this) {
            val db = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "dogday_db"
            ).addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // 第一次创建数据库时插入默认标签
                    CoroutineScope(Dispatchers.IO).launch {
                        val dao = getDatabase(context).dogDao()
                        dao.insertTag(Tag(name = "洗澡", color = 0xFF4A90E2.toInt(), orderIndex = 1))
                        dao.insertTag(Tag(name = "驱虫", color = 0xFF7ED321.toInt(), orderIndex = 2))
                    }
                }
            }).build()
            instance = db
            db
        }
    }
}