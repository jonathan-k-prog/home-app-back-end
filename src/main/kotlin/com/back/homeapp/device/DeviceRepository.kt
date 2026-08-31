package com.back.homeapp.device

import org.springframework.data.jpa.repository.JpaRepository

interface DeviceRepository : JpaRepository<Device, Long> {
    fun findAllByRoomId(roomId: Long): List<Device>

    fun findAllByConnected(connected: Boolean): List<Device>

    fun findAllByRoom_Home_Id(homeId: Long): List<Device>

    fun findByIdentifierAndRoom_Home_Identifier(
        identifier: String,
        homeIdentifier: String,
    ): Device?
}
