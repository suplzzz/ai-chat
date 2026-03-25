package com.suplz.aichat.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.suplz.aichat.data.local.entity.MessageEntity
import com.suplz.aichat.domain.model.Message


@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY createdAt DESC")
    fun getMessagesPaged(chatId: String): PagingSource<Int, MessageEntity>

    @Query("SELECT * FROM messages WHERE type = 'IMAGE' ORDER BY createdAt DESC")
    fun getAllImagesPaged(): PagingSource<Int, MessageEntity>

    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY createdAt DESC LIMIT 20")
    suspend fun getRecentMessagesSync(chatId: String): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("UPDATE messages SET status = :status WHERE id = :messageId")
    suspend fun updateMessageStatus(messageId: String, status: Message.MessageStatus)

    @Query("UPDATE messages SET text = :text, imageUrl = :imageUrl, type = :type, status = :status WHERE id = :messageId")
    suspend fun updateMessageContent(messageId: String, text: String, imageUrl: String?, type: Message.MessageType, status: Message.MessageStatus)

    @Query("SELECT * FROM messages WHERE id = :messageId")
    suspend fun getMessageById(messageId: String): MessageEntity?
}