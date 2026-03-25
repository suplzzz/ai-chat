package com.suplz.aichat.domain.usecase.message

import androidx.paging.PagingData
import com.suplz.aichat.domain.model.Message
import com.suplz.aichat.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMessagesUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    operator fun invoke(chatId: String): Flow<PagingData<Message>> =
        messageRepository.getMessages(chatId)
}