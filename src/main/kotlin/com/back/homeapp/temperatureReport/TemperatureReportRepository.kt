package com.back.homeapp.temperatureReport

import org.springframework.data.jpa.repository.JpaRepository

interface TemperatureReportRepository : JpaRepository<TemperatureReport, Long> {
    fun findTopByOrderByTimestampDesc(): TemperatureReport?
}
