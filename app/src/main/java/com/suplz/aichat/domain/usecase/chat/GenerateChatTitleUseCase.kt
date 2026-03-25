package com.suplz.aichat.domain.usecase.chat

import com.suplz.aichat.domain.repository.ChatRepository
import javax.inject.Inject

class GenerateChatTitleUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(chatId: String, firstMessageText: String): Result<Unit> {
        val prompt = "Сгенерируй короткое название для чата (не более 3-4 слов) на основе этого сообщения: \"$firstMessageText\". Ответь только названием, без кавычек и лишних слов."

        return chatRepository.generateTitle(prompt).fold(
            onSuccess = { generatedTitle ->
                chatRepository.updateChatTitle(chatId, generatedTitle)
                Result.success(Unit)
            },
            onFailure = {
                Result.failure(it)
            }
        )
    }
}