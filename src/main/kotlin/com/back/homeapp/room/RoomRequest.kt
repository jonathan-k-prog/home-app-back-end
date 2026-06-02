package com.back.homeapp.room

import com.back.homeapp.roomType.RoomType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class RoomRequest(
    @field:NotBlank(message = "Room name is required")
    @field:Size(max = 100, message = "Room name must be 100 characters or less")
    val name: String,

    @field:NotNull(message = "Room type is required")
    val type: RoomType,
)
