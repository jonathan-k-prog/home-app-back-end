package com.back.homeapp.home

import com.back.homeapp.user.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class HomeService(
    val homeRepository: HomeRepository,
    val userRepository: UserRepository,
) {
    fun create(homeRequest: HomeRequest, userEmail: String): HomeResponse {
        val creator = userRepository.findByEmail(userEmail)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User $userEmail not found")

        val home = homeRepository.save(Home(
            name = homeRequest.name,
            identifier = homeRequest.identifier,
            creator = creator
        ))

        return home.toResponse()
    }

    fun findAll(): List<HomeResponse> =
        homeRepository
            .findAll()
            .sortedBy { it.name.lowercase() }
            .map { it.toResponse() }

    fun findById(id: Long): HomeResponse =
        homeRepository
            .findById(id)
            .orElseThrow { throw Exception("Home $id not found") }
            .toResponse()


    fun update(
        id: Long,
        request: HomeRequest,
    ): HomeResponse {
        val home =
            homeRepository
                .findById(id)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Home $id not found") }

        home.name = request.name
        home.identifier = request.identifier

        return homeRepository.save(home).toResponse()
    }

    fun delete(id: Long): HomeResponse {
        val home =
            homeRepository
                .findById(id)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Home $id not found") }

        val response = home.toResponse()
        homeRepository.delete(home)

        return response
    }
}