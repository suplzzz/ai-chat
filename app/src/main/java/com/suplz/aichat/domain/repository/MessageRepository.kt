package com.suplz.aichat.domain.repository

import androidx.paging.PagingData
import com.suplz.aichat.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    fun getMessages(chatId: String): Flow<PagingData<Message>>
    suspend fun sendMessage(chatId: String, text: String, isNewChat: Boolean): Result<Unit>
    suspend fun resendMessage(messageId: String): Result<Unit>
}