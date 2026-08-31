package com.back.homeapp.notification

import com.back.homeapp.notificationType.NotificationType
import java.time.Instant

data class NotificationResponse(
    val id: Long?,
    val type: NotificationType,
    val message: String,
    val timestamp: Instant,
    val read: Boolean,
)
