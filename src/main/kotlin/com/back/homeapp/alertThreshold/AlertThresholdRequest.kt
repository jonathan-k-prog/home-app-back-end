package com.back.homeapp.alertThreshold

data class AlertThresholdRequest(
    val metric: AlertThresholdMetric,
    val minValue: Double?,
    val maxValue: Double?,
    val deviceId: Long,
)
