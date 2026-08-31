package com.back.homeapp.notification

import com.back.homeapp.notificationType.NotificationType
import java.time.Instant

data class NotificationRequest(
    var type: NotificationType,
    var message: String,
    var timestamp: Instant,
    var read: Boolean,
)
