package com.back.homeapp.device

import com.back.homeapp.alertThreshold.AlertThreshold
import com.back.homeapp.deviceType.DeviceType
import com.back.homeapp.humidityReport.HumidityReport
import com.back.homeapp.room.Room
import com.back.homeapp.statistics.ReportStatistics
import com.back.homeapp.temperatureReport.TemperatureReport
import jakarta.persistence.CascadeType
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
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.ColumnDefault
import java.time.Instant

@Entity
@Table(name = "devices")
class Device(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false)
    var name: String = "",
    @Column(nullable = false)
    var identifier: String = "",
    @ColumnDefault("false")
    @Column(nullable = false)
    var connected: Boolean = false,
    @ColumnDefault("now()")
    @Column(nullable = false)
    var lastSeen: Instant = Instant.now(),
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: DeviceType = DeviceType.DEFAULT,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roomId", nullable = false)
    var room: Room,
    @OneToMany(mappedBy = "device", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val humidityReports: MutableList<HumidityReport> = mutableListOf(),
    @OneToMany(mappedBy = "device", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val temperatureReports: MutableList<TemperatureReport> = mutableListOf(),
    @OneToMany(mappedBy = "device", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val alertThresholds: MutableList<AlertThreshold> = mutableListOf(),
) {
    fun markConnected(at: Instant) {
        connected = true
        lastSeen = at
    }

    fun markDisconnected() {
        connected = false
    }

    fun toResponse() =
        DeviceResponse(
            id = id,
            name = name,
            identifier = identifier,
            connected = connected,
            lastSeen = lastSeen,
            type = type,
            room = room.toResponse(),
            averageTemperature = ReportStatistics.average(temperatureReports) { it.value },
            minTemperatureReport = ReportStatistics.min(temperatureReports) { it.value }?.toResponse(),
            maxTemperatureReport = ReportStatistics.max(temperatureReports) { it.value }?.toResponse(),
            lastTemperatureReport = ReportStatistics.last(temperatureReports) { it.timestamp }?.toResponse(),
            averageHumidity = ReportStatistics.average(humidityReports) { it.value },
            minHumidityReport = ReportStatistics.min(humidityReports) { it.value }?.toResponse(),
            maxHumidityReport = ReportStatistics.max(humidityReports) { it.value }?.toResponse(),
            lastHumidityReport = ReportStatistics.last(humidityReports) { it.timestamp }?.toResponse(),
            alertThresholds = alertThresholds.map { it.toResponse() },
        )
}
