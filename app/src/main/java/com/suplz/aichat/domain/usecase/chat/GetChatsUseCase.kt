package com.suplz.aichat.domain.usecase.chat

import androidx.paging.PagingData
import com.suplz.aichat.domain.model.Chat
import com.suplz.aichat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatsUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(): Flow<PagingData<Chat>> = chatRepository.getChats()
}