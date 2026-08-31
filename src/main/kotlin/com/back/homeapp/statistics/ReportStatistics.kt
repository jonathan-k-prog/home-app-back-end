package com.back.homeapp.statistics

import java.time.Instant

object ReportStatistics {
    fun <T> average(
        reports: List<T>,
        valueOf: (T) -> Double,
    ): Double = if (reports.isEmpty()) 0.0 else reports.map(valueOf).average()

    fun <T> min(
        reports: List<T>,
        valueOf: (T) -> Double,
    ): T? = reports.minByOrNull(valueOf)

    fun <T> max(
        reports: List<T>,
        valueOf: (T) -> Double,
    ): T? = reports.maxByOrNull(valueOf)

    fun <T> last(
        reports: List<T>,
        timestampOf: (T) -> Instant,
    ): T? = reports.maxByOrNull(timestampOf)
}
