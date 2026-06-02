package com.back.homeapp.room

import com.back.homeapp.device.Device
import com.back.homeapp.humidityReport.HumidityReport
import com.back.homeapp.roomType.RoomType
import com.back.homeapp.temperatureReport.TemperatureReport
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "rooms")
class Room(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var name: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: RoomType = RoomType.DEFAULT,

    @OneToMany(mappedBy = "room", cascade = [CascadeType.ALL], orphanRemoval = true)
    val devices: MutableList<Device> = mutableListOf(),
){
    fun toResponse() = RoomResponse(
        id = id,
        name = name,
        type = type,
    )
}
