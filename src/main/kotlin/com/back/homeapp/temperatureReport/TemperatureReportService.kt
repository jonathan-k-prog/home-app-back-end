package com.back.homeapp.temperatureReport

import com.back.homeapp.device.DeviceRepository
import org.springframework.stereotype.Service

@Service
class TemperatureReportService(
    private val temperatureReportRepository: TemperatureReportRepository,
    private val deviceRepository: DeviceRepository
) {
    fun findAll(): List<TemperatureReportResponse> =
        temperatureReportRepository.findAll()
            .sortedByDescending { it.timestamp }
            .map { it.toResponse() }

    fun create(temperatureReportRequest: TemperatureReportRequest): TemperatureReportResponse {
        val device = deviceRepository.findById(temperatureReportRequest.deviceId)
            .orElseThrow { IllegalArgumentException("Device ${temperatureReportRequest.deviceId} not found") }

        return temperatureReportRepository.save(TemperatureReport(
            value = temperatureReportRequest.value,
            timestamp = temperatureReportRequest.timestamp,
            device = device
        )).toResponse()
    }
}
