package com.back.homeapp.alertThreshold

import com.back.homeapp.device.DeviceRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

/**
 * Service class for managing alert thresholds.
 *
 * This service provides methods to create, retrieve, update, and delete alert thresholds associated with devices.
 *
 * @property alertThresholdRepository The repository for accessing alert threshold data.
 * @property deviceRepository The repository for accessing device data.
 */
@Service
class AlertThresholdService(
    private val alertThresholdRepository: AlertThresholdRepository,
    private val deviceRepository: DeviceRepository,
) {
    fun create(request: AlertThresholdRequest): AlertThresholdResponse {
        val device =
            deviceRepository
                .findById(request.deviceId)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Device ${request.deviceId} not found") }

        return alertThresholdRepository
            .save(
                AlertThreshold(
                    metric = request.metric,
                    minValue = request.minValue,
                    maxValue = request.maxValue,
                    device = device,
                ),
            ).toResponse()
    }

    fun findAllByDeviceId(deviceId: Long): List<AlertThresholdResponse> =
        alertThresholdRepository.findAllByDeviceId(deviceId).map { it.toResponse() }

    fun update(
        id: Long,
        request: AlertThresholdRequest,
    ): AlertThresholdResponse {
        val threshold =
            alertThresholdRepository
                .findById(id)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "AlertThreshold $id not found") }
        val device =
            deviceRepository
                .findById(request.deviceId)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Device ${request.deviceId} not found") }

        threshold.metric = request.metric
        threshold.minValue = request.minValue
        threshold.maxValue = request.maxValue
        threshold.device = device

        return alertThresholdRepository.save(threshold).toResponse()
    }

    fun delete(id: Long): AlertThresholdResponse {
        val threshold =
            alertThresholdRepository
                .findById(id)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "AlertThreshold $id not found") }

        val response = threshold.toResponse()
        alertThresholdRepository.delete(threshold)

        return response
    }
}
