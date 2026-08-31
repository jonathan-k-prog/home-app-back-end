package com.back.homeapp.command

import jakarta.validation.constraints.NotBlank

data class CommandRequest(
    val targetType: CommandTargetType,
    val targetId: Long,
    @field:NotBlank val action: String,
)
