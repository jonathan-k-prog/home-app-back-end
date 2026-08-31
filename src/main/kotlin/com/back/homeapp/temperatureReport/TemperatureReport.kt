package com.back.homeapp.temperatureReport

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
@Table(name = "temperature_reports")
class TemperatureReport(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(name = "`value`", nullable = false)
    var value: Double = 0.0,
    @Column(name = "timestamp", nullable = false)
    var timestamp: Instant = Instant.now(),
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deviceId", nullable = false)
    var device: Device,
) {
    fun toResponse() =
        TemperatureReportResponse(
            id = id,
            value = value,
            timestamp = timestamp.toEpochMilli(),
        )
}
