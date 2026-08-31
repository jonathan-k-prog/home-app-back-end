package com.back.homeapp.room

import com.back.homeapp.home.HomeResponse
import com.back.homeapp.roomType.RoomType
import jakarta.persistence.Column

data class RoomResponse(
    val id: Long?,
    val name: String,
    var width: Int,
    var height: Int,
    var x: Int,
    var y: Int,
    var floor: Int,
    val type: RoomType,
    val home: HomeResponse,
)
