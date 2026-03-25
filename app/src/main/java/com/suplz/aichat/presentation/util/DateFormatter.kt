package com.suplz.aichat.presentation.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateFormatter {
    private val formatter = DateTimeFormatter
        .ofPattern("dd.MM.yyyy HH:mm")
        .withZone(ZoneId.systemDefault())

    fun formatTime(timestamp: Long): String {
        return formatter.format(Instant.ofEpochMilli(timestamp))
    }
}