package com.back.homeapp.user

import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {
    fun findById(id: Long): User =
        userRepository
            .findById(id)
            .orElseThrow { throw Exception("User $id not found")}

}