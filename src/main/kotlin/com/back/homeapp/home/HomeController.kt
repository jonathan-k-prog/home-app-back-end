package com.back.homeapp.home

import com.back.homeapp.apiResponse.ApiResponse
import com.back.homeapp.device.DeviceRequest
import com.back.homeapp.device.DeviceResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/homes")
@Validated
@RestController
class HomeController (
    private val homeService: HomeService,
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Valid @RequestBody request: HomeRequest,
        @AuthenticationPrincipal email: String,
    ): ResponseEntity<ApiResponse<HomeResponse>> {
        val result = homeService.create(request, email)

        val response =
            ApiResponse(
                status = "success",
                message = "Device created successfully",
                data = result,
                errors = null,
            )

        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getAll(
    ): ResponseEntity<ApiResponse<List<HomeResponse>>> {
        val result = homeService.findAll()

        val response =
            ApiResponse(
                status = "success",
                message = "Homes fetched successfully",
                data = result,
                errors = null,
            )

        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun getById(
        @Valid @PathVariable id: Long,
    ): ResponseEntity<ApiResponse<HomeResponse>> {
        val result = homeService.findById(id)

        val response =
            ApiResponse(
                status = "success",
                message = "Device fetched successfully",
                data = result,
                errors = null,
            )

        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    fun update(
        @Valid @PathVariable id: Long,
        @Valid @RequestBody request: HomeRequest,
    ): ResponseEntity<ApiResponse<HomeResponse>> {
        val result = homeService.update(id, request)

        val response =
            ApiResponse(
                status = "success",
                message = "Home updated successfully",
                data = result,
                errors = null,
            )

        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long,
    ): ResponseEntity<ApiResponse<HomeResponse>> {
        val result = homeService.delete(id)

        val response =
            ApiResponse(
                status = "success",
                message = "Home deleted successfully",
                data = result,
                errors = null,
            )

        return ResponseEntity.ok(response)
    }

}