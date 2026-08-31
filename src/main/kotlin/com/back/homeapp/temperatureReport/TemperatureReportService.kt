package com.back.homeapp.temperatureReport

import com.back.homeapp.device.DeviceRepository
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class TemperatureReportService(
    private val temperatureReportRepository: TemperatureReportRepository,
    private val deviceRepository: DeviceRepository,
) {
    fun findAll(pageable: Pageable): List<TemperatureReportResponse> =
        temperatureReportRepository
            .findAllByOrderByTimestampDesc(pageable)
            .sortedByDescending { it.timestamp }
            .map { it.toResponse() }

    fun findLatestByDeviceId(deviceId: Long): TemperatureReportResponse {
        val report =
            temperatureReportRepository.findTopByDeviceIdOrderByTimestampDesc(deviceId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No temperature report found for device$deviceId")
        return report.toResponse()
    }

    fun create(temperatureReportRequest: TemperatureReportRequest): TemperatureReportResponse {
        val device =
            deviceRepository
                .findById(temperatureReportRequest.deviceId)
                .orElseThrow { IllegalArgumentException("Device ${temperatureReportRequest.deviceId} not found") }

        return temperatureReportRepository
            .save(
                TemperatureReport(
                    value = temperatureReportRequest.value,
                    timestamp = temperatureReportRequest.timestamp,
                    device = device,
                ),
            ).toResponse()
    }
}
