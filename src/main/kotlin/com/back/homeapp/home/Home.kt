package com.back.homeapp.home

import com.back.homeapp.homeMember.HomeMember
import com.back.homeapp.homeMemberInvitation.HomeMemberInvitation
import com.back.homeapp.room.Room
import com.back.homeapp.user.User
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "homes")
class Home (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false)
    var name: String = "",
    @Column(nullable = false)
    var identifier: String = "",
    @Column(nullable = false)
    var timestamp: Instant = Instant.now(),
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    var creator: User,
    @OneToMany(mappedBy = "home", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val rooms: MutableList<Room> = mutableListOf(),
    @OneToMany(mappedBy = "home", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val members: MutableList<HomeMember> = mutableListOf(),
    @OneToMany(mappedBy = "home", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val invitations: MutableList<HomeMemberInvitation> = mutableListOf(),
) {
    fun toResponse() =
        HomeResponse(
            id = id,
            name = name,
            identifier = identifier,
            timestamp = timestamp,
        )
}