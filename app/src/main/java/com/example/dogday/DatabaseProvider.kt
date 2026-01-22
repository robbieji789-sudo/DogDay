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
        // 如果实例已经存在，直接返回；不存在则创建
        return instance ?: synchronized(this) {
            val db = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "dogday_db"
            ).addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // 只有在数据库第一次被创建时，插入初始标签
                    CoroutineScope(Dispatchers.IO).launch {
                        val dao = getDatabase(context).dogDayDao()
                        dao.insertTag(Tag(name = "洗澡", colorHex = "#4A90E2", order = 1))
                        dao.insertTag(Tag(name = "驱虫", colorHex = "#7ED321", order = 2))
                    }
                }
            }).build()
            instance = db
            db
        }
    }
}