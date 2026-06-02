package com.back.homeapp.humidityReport

import com.back.homeapp.device.DeviceRepository
import org.springframework.stereotype.Service

@Service
class HumidityReportService(
    private val humidityReportRepository: HumidityReportRepository,
    private val deviceRepository: DeviceRepository
) {
    fun findAll(): List<HumidityReportResponse> =
        humidityReportRepository.findAll()
            .sortedByDescending { it.timestamp }
            .map { it.toResponse() }


    fun create(humidityReportRequest: HumidityReportRequest): HumidityReportResponse {
        val device = deviceRepository.findById(humidityReportRequest.deviceId)
            .orElseThrow { IllegalArgumentException("Device ${humidityReportRequest.deviceId} not found") }

        return humidityReportRepository.save(HumidityReport(
            value = humidityReportRequest.value,
            timestamp = humidityReportRequest.timestamp,
            device = device
        )).toResponse()
    }
}
