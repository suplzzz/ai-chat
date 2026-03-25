package com.suplz.aichat.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.suplz.aichat.data.local.dao.ChatDao
import com.suplz.aichat.data.local.dao.MessageDao
import com.suplz.aichat.data.local.entity.ChatEntity
import com.suplz.aichat.data.local.entity.MessageEntity

@Database(
    entities = [ChatEntity::class, MessageEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
}