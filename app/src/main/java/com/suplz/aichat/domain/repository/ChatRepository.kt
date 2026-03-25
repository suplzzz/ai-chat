package com.suplz.aichat.domain.repository

import androidx.paging.PagingData
import com.suplz.aichat.domain.model.Chat
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChats(): Flow<PagingData<Chat>>
    fun searchChats(query: String): Flow<PagingData<Chat>>
    fun getChatTitle(chatId: String): Flow<String?>
    suspend fun updateChatTitle(chatId: String, newTitle: String)
    suspend fun generateTitle(prompt: String): Result<String>
}