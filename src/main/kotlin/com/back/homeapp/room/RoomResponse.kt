package com.back.homeapp.room

import com.back.homeapp.roomType.RoomType

data class RoomResponse(
    val id: Long?,
    val name: String,
    val type: RoomType,
)
