package com.back.homeapp.temperatureReport

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface TemperatureReportRepository : JpaRepository<TemperatureReport, Long> {
    fun findTopByOrderByTimestampDesc(): TemperatureReport?

    fun findAllByOrderByTimestampDesc(pageable: Pageable): Page<TemperatureReport>

    fun findTopByDeviceIdOrderByTimestampDesc(deviceId: Long): TemperatureReport?
}
