package com.back.homeapp.alertThreshold

import org.springframework.data.jpa.repository.JpaRepository

interface AlertThresholdRepository : JpaRepository<AlertThreshold, Long> {
    fun findAllByDeviceId(deviceId: Long): List<AlertThreshold>
}
