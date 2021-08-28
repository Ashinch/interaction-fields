package com.interactionfields.user.service

import com.interactionfields.auth.common.util.BCryptPasswordEncoderExt.encodeBCrypt
import com.interactionfields.auth.common.util.RolesID
import com.interactionfields.common.domain.User
import com.interactionfields.common.domain.UserRole
import com.interactionfields.common.repository.UserRepository.users
import com.interactionfields.common.repository.UserRoleRepository.userRoles
import org.ktorm.database.Database
import org.ktorm.entity.add
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class UserService(private val db: Database) {

    /**
     * Create a [user] and assign roles.
     */
    fun signUp(user: User): Boolean = db.users.add(user.apply {
        uuid = UUID.randomUUID().toString()
        password = password.encodeBCrypt()
        joinedAt = LocalDateTime.now()
        loggedAt = joinedAt
    }) > 0 && db.userRoles.add(UserRole().apply {
        userUUID = user.uuid
        roleID = RolesID.USER
    }) > 0
}
