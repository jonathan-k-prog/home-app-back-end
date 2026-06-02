package com.back.homeapp.humidityReport

import org.springframework.data.jpa.repository.JpaRepository

interface HumidityReportRepository : JpaRepository<HumidityReport, Long> {
}
