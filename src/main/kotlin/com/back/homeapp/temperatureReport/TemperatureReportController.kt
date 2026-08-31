package com.back.homeapp.temperatureReport

import com.back.homeapp.apiResponse.ApiResponse
import com.back.homeapp.page.PageResponse
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/temperature-reports")
class TemperatureReportController(
    private val temperatureReportService: TemperatureReportService,
) {
    @GetMapping
    fun getAll(
        @PageableDefault(
            size = 20,
            sort = ["timestamp"],
            direction = Sort.Direction.DESC,
        ) pageable: Pageable,
    ): ResponseEntity<ApiResponse<PageResponse<TemperatureReportResponse>>> {
        val temperatureReports = temperatureReportService.findAll(pageable)

        val pageResponse =
            PageResponse(
                content = temperatureReports,
                page = pageable.pageNumber,
                size = pageable.pageSize,
                totalElements = temperatureReports.size.toLong(),
                totalPages = (temperatureReports.size + pageable.pageSize - 1) / pageable.pageSize,
            )

        val response =
            ApiResponse(
                status = "success",
                message = "Room updated successfully",
                data = pageResponse,
                errors = null,
            )

        return ResponseEntity.ok(response)
    }

    @GetMapping("/latest/{deviceId}")
    fun getLatestByDeviceId(
        @NotNull @Positive @PathVariable deviceId: Long,
    ): ResponseEntity<ApiResponse<TemperatureReportResponse>> {
        val result = temperatureReportService.findLatestByDeviceId(deviceId)

        val response =
            ApiResponse(
                status = "success",
                message = "Latest Temperature Report from deviceId fetched successfully",
                data = result,
                errors = null,
            )

        return ResponseEntity.ok(response)
    }
}
