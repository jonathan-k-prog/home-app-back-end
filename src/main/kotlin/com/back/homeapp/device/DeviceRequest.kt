package com.back.homeapp.device

import com.back.homeapp.deviceType.DeviceType
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class DeviceRequest(
    @field:NotBlank(message = "Device name is required")
    @field:Size(max = 100, message = "Device name must be 100 characters or less")
    var name: String,

    @field:NotNull(message = "Device type is required")
    var type: DeviceType,

    @field:NotNull(message = "Room id is required")
    @field:Min(value = 1, message = "Room id must be greater than 0")
    var roomId: Long,
)
