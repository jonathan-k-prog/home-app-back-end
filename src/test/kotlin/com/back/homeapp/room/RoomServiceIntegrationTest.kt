package com.back.homeapp.room

import com.back.homeapp.home.Home
import com.back.homeapp.home.HomeRepository
import com.back.homeapp.roomType.RoomType
import com.back.homeapp.user.User
import com.back.homeapp.user.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import java.time.Instant

@SpringBootTest
@TestPropertySource(properties = ["mqtt.enabled=false"])
class RoomServiceIntegrationTest(
    @Autowired private val roomService: RoomService,
    @Autowired private val roomRepository: RoomRepository,
    @Autowired private val homeRepository: HomeRepository,
    @Autowired private val userRepository: UserRepository,
    ) {
    @Test
    fun `should create update list and delete a room`() {
        roomRepository.deleteAll()
        homeRepository.deleteAll()
        userRepository.deleteAll()

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

        val createPayload =
            RoomRequest(
                name = "Salon",
                type = RoomType.DEFAULT,
                width = 0,
                height = 0,
                x = 0,
                y = 0,
                floor = 0,
                homeId = homeOne.id!!
            )

        val createdRoom = roomService.create(createPayload)

        assertEquals("Salon", createdRoom.name)
        assertEquals(RoomType.DEFAULT, createdRoom.type)

        val fetchedRoom = roomService.findById(createdRoom.id!!)
        assertEquals(createdRoom.id, fetchedRoom.id)

        val updatePayload =
            RoomRequest(
                name = "Salon new",
                type = RoomType.DEFAULT,
                width = 0,
                height = 0,
                x = 0,
                y = 0,
                floor = 0,
                homeId = homeOne.id!!
            )

        val updatedRoom = roomService.update(createdRoom.id!!, updatePayload)
        assertEquals("Salon new", updatedRoom.name)
        assertEquals(RoomType.DEFAULT, updatedRoom.type)

        val rooms = roomService.findAll(null)
        assertEquals(1, rooms.size)
        assertEquals("Salon new", rooms.first().name)

        roomService.delete(createdRoom.id!!)
        assertEquals(0, roomService.findAll(null).size)
    }

    @Test
    fun `should accept duplicate room names`() {
        roomRepository.deleteAll()
        homeRepository.deleteAll()
        userRepository.deleteAll()

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

        roomRepository.save(Room(
            name = "Cuisine",
            type = RoomType.DEFAULT,
            width = 0,
            height = 0,
            floor = 0,
            x = 0,
            y = 0,
            devices = mutableListOf(),
            home = homeOne
        ))

        val duplicatePayload =
            RoomRequest(
                name = "Cuisine",
                type = RoomType.DEFAULT,
                width = 0,
                height = 0,
                x = 0,
                y = 0,
                floor = 0,
                homeId = homeOne.id!!
            )

        val createdRoom = roomService.create(duplicatePayload)

        assertEquals("Cuisine", createdRoom.name)
        assertEquals(RoomType.DEFAULT, createdRoom.type)

        val rooms = roomService.findAll(null)
        assertEquals(2, rooms.size)
    }
}
