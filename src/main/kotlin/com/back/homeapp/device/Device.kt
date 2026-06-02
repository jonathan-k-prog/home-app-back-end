package com.back.homeapp.device

import com.back.homeapp.deviceType.DeviceType
import com.back.homeapp.humidityReport.HumidityReport
import com.back.homeapp.room.Room
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

@Entity
@Table(name = "devices")
class Device(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var name: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: DeviceType = DeviceType.DEFAULT,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "roomId", nullable = false)
    var room: Room,

    @OneToMany(mappedBy = "device", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    val humidityReports: MutableList<HumidityReport> = mutableListOf(),

    @OneToMany(mappedBy = "device", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    val temperatureReports: MutableList<TemperatureReport> = mutableListOf()
){
    fun toResponse() = DeviceResponse(
        id = id,
        name = name,
        type = type,
        room = room.toResponse(),
        humidityReports = humidityReports.map { it.toResponse() },
        temperatureReports = temperatureReports.map { it.toResponse() }
    )
}
