package com.suplz.aichat.presentation.main

sealed class Screen(val route: String) {
    object Auth : Screen("auth")

    object ChatList : Screen("chat_list?focusSearch={focusSearch}") {
        fun createRoute(focusSearch: Boolean = false) = "chat_list?focusSearch=$focusSearch"
    }

    object Chat : Screen("chat/{chatId}?isNew={isNew}") {
        fun createRoute(chatId: String, isNew: Boolean = false) = "chat/$chatId?isNew=$isNew"
    }

    object Profile : Screen("profile")

    object ImageGallery : Screen("image_gallery")
}