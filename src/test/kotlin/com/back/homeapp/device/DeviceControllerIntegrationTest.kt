package com.back.homeapp.device

import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.transaction.annotation.Transactional
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = ["mqtt.enabled=false"])
@Transactional
class DeviceControllerIntegrationTest(
    @Autowired private val mockMvc: MockMvc
) {
    @Test
    fun `should reject invalid device request - ADD`() {
        val payload = """
              {
                "name": "",
                "type": "",
                "roomId": 0
              }
          """.trimIndent()

        mockMvc.post("/api/devices") {
            contentType = MediaType.APPLICATION_JSON
            content = payload
        }
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.status") { value("error") }
                jsonPath("$.message") { value("Invalid request body") }
                jsonPath("$.errors.body") { value(containsString("Invalid JSON or unsupported value")) }
            }
    }

    @Test
    fun `should reject device creation when room does not exist`() {
        val payload = """
              {
                "name": "DHT11",
                "type": "ESP_32_DHT11",
                "roomId": 999
              }
          """.trimIndent()

        mockMvc.post("/api/devices") {
            contentType = MediaType.APPLICATION_JSON
            content = payload
        }
            .andExpect {
                status { isNotFound() }
                jsonPath("$.status") { value("error") }
                jsonPath("$.message") { value("Room 999 not found") }
            }
    }

    @Test
    fun `should reject invalid device id parameter - GET`() {
        mockMvc.get("/api/devices/abc")
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.status") { value("error") }
                jsonPath("$.message") { value("Invalid parameter") }
                jsonPath("$.errors.id") { value("Invalid value") }
            }
    }

    @Test
    fun `should reject invalid device id parameter - UPDATE`() {
        val payload = """
              {
                "name": "test",
                "type": "0",
                "roomId": 1
              }
          """.trimIndent()

        mockMvc.put("/api/devices/abc") {
            contentType = MediaType.APPLICATION_JSON
            content = payload
        }
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.status") { value("error") }
                jsonPath("$.message") { value("Invalid parameter") }
                jsonPath("$.errors.id") { value("Invalid value") }
            }
    }

    @Test
    fun `should reject invalid device request - UPDATE`() {
        val payload = """
              {
                "name": "",
                "type": "",
                "roomId": 0
              }
          """.trimIndent()

        mockMvc.put("/api/devices/1") {
            contentType = MediaType.APPLICATION_JSON
            content = payload
        }
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.status") { value("error") }
                jsonPath("$.message") { value("Invalid request body") }
                jsonPath("$.errors.body") { value(containsString("Invalid JSON or unsupported value")) }
            }
    }

    @Test
    fun `should reject invalid device id parameter - DELETE`() {
        mockMvc.delete("/api/devices/abc")
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.status") { value("error") }
                jsonPath("$.message") { value("Invalid parameter") }
                jsonPath("$.errors.id") { value("Invalid value") }
            }
    }
}
