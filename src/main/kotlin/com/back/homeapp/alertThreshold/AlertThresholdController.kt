package com.back.homeapp.alertThreshold

import com.back.homeapp.apiResponse.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/alert-thresholds")
class AlertThresholdController(
    private val alertThresholdService: AlertThresholdService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestBody request: AlertThresholdRequest,
    ): ResponseEntity<ApiResponse<AlertThresholdResponse>> {
        val result = alertThresholdService.create(request)

        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse(status = "success", message = "AlertThreshold created successfully", data = result, errors = null),
        )
    }

    @GetMapping("/device/{deviceId}")
    fun getAllByDevice(
        @PathVariable deviceId: Long,
    ): ResponseEntity<ApiResponse<List<AlertThresholdResponse>>> {
        val result = alertThresholdService.findAllByDeviceId(deviceId)

        return ResponseEntity.ok(
            ApiResponse(status = "success", message = "AlertThresholds fetched successfully", data = result, errors = null),
        )
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody request: AlertThresholdRequest,
    ): ResponseEntity<ApiResponse<AlertThresholdResponse>> {
        val result = alertThresholdService.update(id, request)

        return ResponseEntity.ok(
            ApiResponse(status = "success", message = "AlertThreshold updated successfully", data = result, errors = null),
        )
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long,
    ): ResponseEntity<ApiResponse<AlertThresholdResponse>> {
        val result = alertThresholdService.delete(id)

        return ResponseEntity.ok(
            ApiResponse(status = "success", message = "AlertThreshold deleted successfully", data = result, errors = null),
        )
    }
}
