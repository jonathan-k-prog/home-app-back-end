package com.back.homeapp.notification

import com.back.homeapp.notificationType.NotificationType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "notifications")
class Notification(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var type: NotificationType,
    @Column(nullable = false)
    var message: String = "",
    @Column(nullable = false)
    var timestamp: Instant,
    @Column(nullable = false)
    var read: Boolean = false,
) {
    fun toResponse() =
        NotificationResponse(
            id = id,
            type = type,
            message = message,
            timestamp = timestamp,
            read = read,
        )
}
