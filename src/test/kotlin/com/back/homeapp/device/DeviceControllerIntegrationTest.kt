package com.back.homeapp.device

import com.back.homeapp.deviceType.DeviceType
import com.back.homeapp.home.Home
import com.back.homeapp.home.HomeRepository
import com.back.homeapp.room.Room
import com.back.homeapp.room.RoomRepository
import com.back.homeapp.roomType.RoomType
import com.back.homeapp.security.JwtService
import com.back.homeapp.user.User
import com.back.homeapp.user.UserRepository
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = ["mqtt.enabled=false"])
@Transactional
class DeviceControllerIntegrationTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val deviceRepository: DeviceRepository,
    @Autowired private val roomRepository: RoomRepository,
    @Autowired private val homeRepository: HomeRepository,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val jwtService: JwtService,
) {
    private val authHeader get() = "Bearer " + jwtService.generateToken("test@example.com")

    @Test
    fun `should filter devices by roomId`() {
        val userOne = userRepository.save(User(
            email = "plouf@example.com",
            timestamp = Instant.now(),
            googleId = "1234567890",
            name = "Plouf",
            pictureUrl = "https://example.com/plouf.png",
            createdHomes = mutableListOf(),
            homeMemberships = mutableListOf(),
            homeInvitations = mutableListOf(),
        ))

        val homeOne = homeRepository.save(Home(
            name = "Maison 1",
            identifier = "home-1",
            timestamp = Instant.now(),
            invitations = mutableListOf(),
            members = mutableListOf(),
            rooms = mutableListOf(),
            creator = userOne
        ))

        val livingRoom = roomRepository.save(Room(
            name = "Salon",
            type = RoomType.DEFAULT,
            width = 0,
            height = 0,
            floor = 0,
            x = 0,
            y = 0,
            devices = mutableListOf(),
            home = homeOne
        ))

        val bedroom = roomRepository.save(Room(
            name = "Chambre",
            type = RoomType.DEFAULT,
            width = 0,
            height = 0,
            floor = 0,
            x = 0,
            y = 0,
            devices = mutableListOf(),
            home = homeOne
        ))

        deviceRepository.save(Device(
            name = "Salon DHT11",
            type = DeviceType.ESP_32_DHT11,
            room = livingRoom,
            lastSeen = Instant.now()
        ))
        deviceRepository.save(Device(
            name = "Chambre DHT11",
            type = DeviceType.ESP_32_DHT11,
            room = bedroom,
            lastSeen = Instant.now()
        ))

        mockMvc
            .get("/api/devices") {
                header("Authorization", authHeader)
                param("roomId", livingRoom.id.toString())
            }.andExpect {
                status { isOk() }
                jsonPath("$.data.length()") { value(1) }
                jsonPath("$.data[0].name") { value("Salon DHT11") }
                jsonPath("$.data[0].room.id") { value(livingRoom.id) }
            }
    }

    @Test
    fun `should reject invalid device request - ADD`() {
        val payload =
            """
            {
              "name": "",
              "type": "",
              "roomId": 0
            }
            """.trimIndent()

        mockMvc
            .post("/api/devices") {
                header("Authorization", authHeader)
                contentType = MediaType.APPLICATION_JSON
                content = payload
            }.andExpect {
                status { isBadRequest() }
                jsonPath("$.status") { value("error") }
                jsonPath("$.message") { value("Invalid request body") }
                jsonPath("$.errors.body") { value(containsString("Invalid JSON or unsupported value")) }
            }
    }

    @Test
    fun `should reject device creation when room does not exist`() {
        val payload =
            """
            {
              "name": "DHT11",
              "identifier": "esp32-dht11-1",
              "connected": false,
              "lastSeen": "2024-01-01T00:00:00Z",
              "type": "ESP_32_DHT11",
              "roomId": 999
            }
            """.trimIndent()

        mockMvc
            .post("/api/devices") {
                header("Authorization", authHeader)
                contentType = MediaType.APPLICATION_JSON
                content = payload
            }.andExpect {
                status { isNotFound() }
                jsonPath("$.status") { value("error") }
                jsonPath("$.message") { value("Room 999 not found") }
            }
    }

    @Test
    fun `should reject invalid device id parameter - GET`() {
        mockMvc
            .get("/api/devices/abc") {
                header("Authorization", authHeader)
            }.andExpect {
                status { isBadRequest() }
                jsonPath("$.status") { value("error") }
                jsonPath("$.message") { value("Invalid parameter") }
                jsonPath("$.errors.id") { value("Invalid value") }
            }
    }

    @Test
    fun `should reject invalid device id parameter - UPDATE`() {
        val payload =
            """
            {
              "name": "test",
              "type": "0",
              "roomId": 1
            }
            """.trimIndent()

        mockMvc
            .put("/api/devices/abc") {
                header("Authorization", authHeader)
                contentType = MediaType.APPLICATION_JSON
                content = payload
            }.andExpect {
                status { isBadRequest() }
                jsonPath("$.status") { value("error") }
                jsonPath("$.message") { value("Invalid parameter") }
                jsonPath("$.errors.id") { value("Invalid value") }
            }
    }

    @Test
    fun `should reject invalid device request - UPDATE`() {
        val payload =
            """
            {
              "name": "",
              "type": "",
              "roomId": 0
            }
            """.trimIndent()

        mockMvc
            .put("/api/devices/1") {
                header("Authorization", authHeader)
                contentType = MediaType.APPLICATION_JSON
                content = payload
            }.andExpect {
                status { isBadRequest() }
                jsonPath("$.status") { value("error") }
                jsonPath("$.message") { value("Invalid request body") }
                jsonPath("$.errors.body") { value(containsString("Invalid JSON or unsupported value")) }
            }
    }

    @Test
    fun `should reject invalid device id parameter - DELETE`() {
        mockMvc
            .delete("/api/devices/abc") {
                header("Authorization", authHeader)
            }.andExpect {
                status { isBadRequest() }
                jsonPath("$.status") { value("error") }
                jsonPath("$.message") { value("Invalid parameter") }
                jsonPath("$.errors.id") { value("Invalid value") }
            }
    }
}
