package com.back.homeapp.room

import com.back.homeapp.roomType.RoomType
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class RoomRequest(
    @field:NotBlank(message = "Room name is required")
    @field:Size(max = 100, message = "Room name must be 100 characters or less")
    val name: String,
    @field:NotNull(message = "Room width is required")
    @field:Min(value = 1, message = "Room width must be greater than 0")
    var width: Int,
    @field:NotNull(message = "Room height required")
    @field:Min(value = 1, message = "Room height must be greater than 0")
    var height: Int ,
    @field:NotNull(message = "Room x is required")
    @field:Min(value = 0, message = "Room x must be greater or equals than 0")
    var x: Int,
    @field:NotNull(message = "Room y is required")
    @field:Min(value = 0, message = "Room y must be greater or equals than 0")
    var y: Int,
    @field:NotNull(message = "Room floor is required")
    var floor: Int,
    @field:NotNull(message = "Room type is required")
    var type: RoomType,
    @field:NotNull(message = "Room type is required")
    var homeId: Long,
)
