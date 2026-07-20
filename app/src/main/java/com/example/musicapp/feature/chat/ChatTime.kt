package com.example.musicapp.feature.chat

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Formats a message timestamp as a short wall-clock time (e.g. "14:05"). */
fun formatChatTime(timestamp: Long?): String {
    if (timestamp == null || timestamp == 0L) return ""
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}
