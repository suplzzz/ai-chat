package com.suplz.aichat.presentation.chat

import com.suplz.aichat.presentation.util.UiText

data class ChatState(
    val chatId: String = "",
    val title: String = "",
    val inputText: String = "",
    val isSending: Boolean = false
)

sealed interface ChatAction {
    data class OnInputTextChanged(val text: String) : ChatAction
    object OnSendMessage : ChatAction
    data class OnResendMessage(val messageId: String) : ChatAction
}

sealed interface ChatEvent {
    data class ShowError(val message: UiText) : ChatEvent
}