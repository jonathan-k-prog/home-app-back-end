package com.back.homeapp.alertThreshold

data class AlertThresholdResponse(
    val id: Long?,
    val metric: AlertThresholdMetric,
    val minValue: Double?,
    val maxValue: Double?,
    val deviceId: Long,
)
