package com.suplz.aichat.presentation.chat_list

import com.suplz.aichat.presentation.util.UiText

data class ChatListState(
    val searchQuery: String = "",
    val activeSearchQuery: String = ""
)

sealed interface ChatListAction {
    data class OnSearchQueryChange(val query: String) : ChatListAction
    object OnSearchClick : ChatListAction
}

sealed interface ChatListEvent {
    data class NavigateToChat(val chatId: String, val isNew: Boolean = false) : ChatListEvent
    data class ShowError(val message: UiText) : ChatListEvent
}