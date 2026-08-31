package com.back.homeapp.weather

data class WeatherApiResponse(
    val location: WeatherApiLocationResponse,
    val current: WeatherApiCurrentResponse,
)

data class WeatherApiLocationResponse(
    val name: String,
    val region: String,
    val country: String,
    val lat: Float,
    val lon: Float,
    val tz_id: String,
    val localtime_epoch: Long,
    val localtime: String,
)

data class WeatherApiCurrentResponse(
    val last_updated_epoch: Long,
    val last_updated: String,
    val temp_c: Float,
    val temp_f: Float,
    val is_day: Boolean,
    val condition: WeatherApiCurrentConditionResponse,
    val wind_mph: Long,
    val wind_kph: Long,
    val wind_degree: Long,
    val wind_dir: String,
    val pressure_mb: Float,
    val pressure_in: Float,
    val precip_mm: Float,
    val precip_in: Float,
    val humidity: Float,
    val cloud: Int,
    val feelslike_c: Float,
    val feelslike_f: Float,
    val windchill_c: Float,
    val windchill_f: Float,
    val heatindex_c: Float,
    val heatindex_f: Float,
    val dewpoint_c: Float,
    val dewpoint_f: Float,
    val vis_km: Float,
    val vis_miles: Float,
    val uv: Float,
    val gust_mph: Float,
    val gust_kph: Float,
    val will_it_rain: Boolean,
    val chance_of_rain: Int,
    val will_it_snow: Boolean,
    val chance_of_snow: Int,
)

data class WeatherApiCurrentConditionResponse(
    val text: String,
    val icon: String,
    val code: Int,
)
