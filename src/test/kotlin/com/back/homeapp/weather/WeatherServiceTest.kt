package com.back.homeapp.weather

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito.RETURNS_DEEP_STUBS
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientResponseException

class WeatherServiceTest {
    private val properties = WeatherProperties(apiKey = "key-123", baseUrl = "https://weather.example.com", location = "Paris")

    private fun apiResponseFixture() =
        WeatherApiResponse(
            location =
                WeatherApiLocationResponse(
                    name = "Paris",
                    region = "Ile-de-France",
                    country = "France",
                    lat = 48.8f,
                    lon = 2.3f,
                    tz_id = "Europe/Paris",
                    localtime_epoch = 1000L,
                    localtime = "2026-08-23 12:00",
                ),
            current =
                WeatherApiCurrentResponse(
                    last_updated_epoch = 1000L,
                    last_updated = "2026-08-23 12:00",
                    temp_c = 21.5f,
                    temp_f = 70.7f,
                    is_day = true,
                    condition = WeatherApiCurrentConditionResponse(text = "Sunny", icon = "icon.png", code = 1000),
                    wind_mph = 5,
                    wind_kph = 8,
                    wind_degree = 180,
                    wind_dir = "S",
                    pressure_mb = 1015f,
                    pressure_in = 30.0f,
                    precip_mm = 0f,
                    precip_in = 0f,
                    humidity = 55f,
                    cloud = 10,
                    feelslike_c = 21.5f,
                    feelslike_f = 70.7f,
                    windchill_c = 21.5f,
                    windchill_f = 70.7f,
                    heatindex_c = 21.5f,
                    heatindex_f = 70.7f,
                    dewpoint_c = 12f,
                    dewpoint_f = 53.6f,
                    vis_km = 10f,
                    vis_miles = 6f,
                    uv = 4f,
                    gust_mph = 8f,
                    gust_kph = 12f,
                    will_it_rain = false,
                    chance_of_rain = 0,
                    will_it_snow = false,
                    chance_of_snow = 0,
                ),
        )

    @Test
    fun `fetch throws when the api key is not configured`() {
        val restClientBuilder = mock(RestClient.Builder::class.java)
        whenever(restClientBuilder.baseUrl(any<String>())).thenReturn(restClientBuilder)
        whenever(restClientBuilder.build()).thenReturn(mock(RestClient::class.java))

        val weatherService = WeatherService(restClientBuilder, properties.copy(apiKey = ""))

        assertThrows(IllegalStateException::class.java) { weatherService.fetch() }
    }

    @Test
    fun `fetch maps the api response into a WeatherResponse`() {
        val restClientBuilder = mock(RestClient.Builder::class.java)
        val restClient = mock(RestClient::class.java, RETURNS_DEEP_STUBS)
        whenever(restClientBuilder.baseUrl(any<String>())).thenReturn(restClientBuilder)
        whenever(restClientBuilder.build()).thenReturn(restClient)
        whenever(restClient.get().uri(any<String>()).retrieve().body(WeatherApiResponse::class.java))
            .thenReturn(apiResponseFixture())

        val weatherService = WeatherService(restClientBuilder, properties)
        val result = weatherService.fetch()

        assertEquals(21.5f, result.temperature)
        assertEquals("Sunny", result.condition)
        assertEquals(55f, result.humidity)
    }

    @Test
    fun `fetch throws when the api call returns an empty body`() {
        val restClientBuilder = mock(RestClient.Builder::class.java)
        val restClient = mock(RestClient::class.java, RETURNS_DEEP_STUBS)
        whenever(restClientBuilder.baseUrl(any<String>())).thenReturn(restClientBuilder)
        whenever(restClientBuilder.build()).thenReturn(restClient)
        whenever(restClient.get().uri(any<String>()).retrieve().body(WeatherApiResponse::class.java))
            .thenReturn(null)

        val weatherService = WeatherService(restClientBuilder, properties)

        assertThrows(IllegalStateException::class.java) { weatherService.fetch() }
    }

    @Test
    fun `fetch wraps api errors into an IllegalStateException`() {
        val restClientBuilder = mock(RestClient.Builder::class.java)
        val restClient = mock(RestClient::class.java, RETURNS_DEEP_STUBS)
        whenever(restClientBuilder.baseUrl(any<String>())).thenReturn(restClientBuilder)
        whenever(restClientBuilder.build()).thenReturn(restClient)
        whenever(restClient.get().uri(any<String>()).retrieve().body(WeatherApiResponse::class.java))
            .thenThrow(RestClientResponseException("boom", 500, "Internal Server Error", null, null, null))

        val weatherService = WeatherService(restClientBuilder, properties)

        val exception = assertThrows(IllegalStateException::class.java) { weatherService.fetch() }
        assertEquals("Weather API error: 500", exception.message)
    }
}
