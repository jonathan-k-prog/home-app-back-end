package com.back.homeapp.room

import com.back.homeapp.roomType.RoomType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.web.server.ResponseStatusException

@SpringBootTest
@TestPropertySource(properties = ["mqtt.enabled=false"])
class RoomServiceIntegrationTest(
    @Autowired private val roomService: RoomService,
    @Autowired private val roomRepository: RoomRepository
) {
    @Test
    fun `should create update list and delete a room`() {
        roomRepository.deleteAll()

        val createPayload = RoomRequest(
            name = "Salon",
            type = RoomType.DEFAULT,
        )

        val createdRoom = roomService.create(createPayload)

        assertEquals("Salon", createdRoom.name)
        assertEquals(RoomType.DEFAULT, createdRoom.type)

        val fetchedRoom = roomService.findById(createdRoom.id!!)
        assertEquals(createdRoom.id, fetchedRoom.id)

        val updatePayload = RoomRequest(
            name = "Salon renove",
            type = RoomType.DEFAULT,
        )

        val updatedRoom = roomService.update(createdRoom.id, updatePayload)
        assertEquals("Salon renove", updatedRoom.name)
        assertEquals(RoomType.DEFAULT, updatedRoom.type)

        val rooms = roomService.findAll()
        assertEquals(1, rooms.size)
        assertEquals("Salon renove", rooms.first().name)

        roomService.delete(createdRoom.id)
        assertEquals(0, roomService.findAll().size)
    }

    @Test
    fun `should accept duplicate room names`() {
        roomRepository.deleteAll()
        roomRepository.save(Room(name = "Cuisine", type = RoomType.DEFAULT))

        val duplicatePayload = RoomRequest(
            name = "cuisine",
            type = RoomType.DEFAULT
        )

        val createdRoom = roomService.create(duplicatePayload)

        assertEquals("cuisine", createdRoom.name)
        assertEquals(RoomType.DEFAULT, createdRoom.type)

        val rooms = roomService.findAll()
        assertEquals(2, rooms.size)
    }
}
