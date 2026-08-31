package com.back.homeapp.user

import com.back.homeapp.home.Home
import com.back.homeapp.homeMember.HomeMember
import com.back.homeapp.homeMemberInvitation.HomeMemberInvitation
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(nullable = false, unique = true)
    var email: String = "",
    @Column(nullable = false, unique = true)
    var googleId: String = "",
    @Column(nullable = false)
    var name: String = "",
    @Column
    var pictureUrl: String? = null,
    @Column(nullable = false)
    var timestamp: Instant = Instant.now(),
    @OneToMany(mappedBy = "creator", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var createdHomes: MutableList<Home> = mutableListOf(),
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var homeMemberships: MutableList<HomeMember> = mutableListOf(),
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var homeInvitations: MutableList<HomeMemberInvitation> = mutableListOf(),
) {
    fun toResponse() =
        UserResponse(
            id = id,
            email = email,
            name = name,
            pictureUrl = pictureUrl,
        )
}
