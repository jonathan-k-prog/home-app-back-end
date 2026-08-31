package com.back.homeapp.humidityReport

import com.back.homeapp.device.Device
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "humidity_reports")
class HumidityReport(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "`value`", nullable = false)
    val value: Double = 0.0,
    @Column(name = "timestamp", nullable = false)
    val timestamp: Instant = Instant.now(),
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deviceId", nullable = false)
    val device: Device,
) {
    fun toResponse() =
        HumidityReportResponse(
            id = id,
            value = value,
            timestamp = timestamp.toEpochMilli(),
        )
}
