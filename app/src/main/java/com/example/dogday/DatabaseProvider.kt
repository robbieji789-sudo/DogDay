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
            )
                .fallbackToDestructiveMigration()  // 如果发现数据库版本不匹配，就删除旧数据库并重新创建，避免闪退
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
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