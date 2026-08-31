package com.back.homeapp.alertThreshold

import com.back.homeapp.device.Device
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "alert_thresholds")
class AlertThreshold(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var metric: AlertThresholdMetric,
    @Column(nullable = true)
    var minValue: Double? = null,
    @Column(nullable = true)
    var maxValue: Double? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deviceId", nullable = false)
    var device: Device,
) {
    fun toResponse() =
        AlertThresholdResponse(
            id = id,
            metric = metric,
            minValue = minValue,
            maxValue = maxValue,
            deviceId = device.id!!,
        )
}
