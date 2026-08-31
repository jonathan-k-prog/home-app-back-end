package com.back.homeapp.room

import com.back.homeapp.device.Device
import com.back.homeapp.home.Home
import com.back.homeapp.roomType.RoomType
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
@Table(name = "rooms")
class Room(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(nullable = false)
    var name: String = "",
    @Column(nullable = false)
    var width: Int = 25,
    @Column(nullable = false)
    var height: Int = 25,
    @Column(nullable = false)
    var x: Int = 0,
    @Column(nullable = false)
    var y: Int = 0,
    @Column(nullable = false)
    var floor: Int = 0,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: RoomType = RoomType.DEFAULT,
    @OneToMany(mappedBy = "room", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var devices: MutableList<Device> = mutableListOf(),
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "homeId", nullable = false)
    var home: Home,
) {
    fun toResponse() =
        RoomResponse(
            id = id,
            name = name,
            width = width,
            height = height,
            x = x,
            y = y,
            floor = floor,
            type = type,
            home = home.toResponse(),
        )
}
