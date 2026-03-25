package com.suplz.aichat.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.suplz.aichat.data.local.entity.ChatEntity

@Dao
interface ChatDao {
    @Query("SELECT * FROM chats ORDER BY updatedAt DESC")
    fun getChatsPaged(): PagingSource<Int, ChatEntity>

    @Query("SELECT * FROM chats WHERE LOWER(title) LIKE '%' || LOWER(:searchQuery) || '%' ORDER BY updatedAt DESC")
    fun searchChatsPaged(searchQuery: String): PagingSource<Int, ChatEntity>

    @Query("SELECT title FROM chats WHERE id = :chatId")
    fun getChatTitleFlow(chatId: String): kotlinx.coroutines.flow.Flow<String?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertChatIfNotExists(chat: ChatEntity): Long

    @Query("UPDATE chats SET title = :newTitle, updatedAt = :updatedAt WHERE id = :chatId")
    suspend fun updateChatTitle(chatId: String, newTitle: String, updatedAt: Long)

    @Query("UPDATE chats SET updatedAt = :updatedAt WHERE id = :chatId")
    suspend fun updateChatTimestamp(chatId: String, updatedAt: Long)
}