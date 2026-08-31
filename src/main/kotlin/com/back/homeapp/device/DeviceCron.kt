package com.back.homeapp.device

import com.back.homeapp.notification.NotificationRequest
import com.back.homeapp.notification.NotificationService
import com.back.homeapp.notificationType.NotificationType
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class DeviceCron(
    private val deviceService: DeviceService,
    private val notificationService: NotificationService,
) {
    @Scheduled(fixedRate = 30000)
    fun checkDeviceConnectivity() {
        //val devices = deviceService.findAll()
        //val disconnectCutoff = Instant.now().minusSeconds(60)

        /*for (device in devices) {
            if (device.lastSeen.isBefore(disconnectCutoff) && device.connected) {
                notify(NotificationType.WARN, "The device (${device.name}) is disconnected.")
                deviceService.markDisconnected(device.id!!)
            }

            for (threshold in device.alertThresholds) {
                val lastValue = when (threshold.metric) {
                    AlertThresholdMetric.TEMPERATURE -> device.temperatureReports.maxByOrNull { it.timestamp }?.value
                    AlertThresholdMetric.HUMIDITY    -> device.humidityReports.maxByOrNull { it.timestamp }?.value
                }

                if (lastValue == null) continue

                val min = threshold.minValue
                val max = threshold.maxValue

                if (min != null && lastValue < min) {
                    notify(NotificationType.DANGER, "${threshold.metric} too low on ${device.name}: $lastValue (min: $min)")
                } else if (max != null && lastValue > max) {
                    notify(NotificationType.DANGER, "${threshold.metric} too high on ${device.name}: $lastValue (max: $max)")
                }
            }
        }*/
    }

    private fun notify(
        type: NotificationType,
        message: String,
    ) {
        val notification =
            notificationService.create(
                NotificationRequest(type = type, message = message, timestamp = Instant.now(), read = false),
            )
        notificationService.publish(notification)
    }
}
