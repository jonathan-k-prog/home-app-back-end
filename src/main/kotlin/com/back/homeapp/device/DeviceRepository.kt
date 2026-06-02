package com.back.homeapp.device

import org.springframework.data.jpa.repository.JpaRepository

interface DeviceRepository : JpaRepository<Device, Long> {
    fun findAllByRoomId(roomId: Long): List<Device>
}
