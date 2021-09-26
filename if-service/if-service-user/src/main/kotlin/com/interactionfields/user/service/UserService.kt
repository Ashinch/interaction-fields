package com.interactionfields.user.service

import com.interactionfields.auth.common.util.BCryptPasswordEncoderExt.encodeBCrypt
import com.interactionfields.common.domain.Role
import com.interactionfields.common.domain.User
import com.interactionfields.common.domain.UserRole
import com.interactionfields.common.repository.RoleRepository
import com.interactionfields.common.repository.UserRepository.users
import com.interactionfields.common.repository.UserRoleRepository.userRoles
import org.ktorm.database.Database
import org.ktorm.entity.add
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.Assert
import java.time.LocalDateTime
import java.util.*

@Service
class UserService(private val db: Database) {

    /**
     * Create a [user] and assign roles.
     */
    @Transactional(rollbackFor = [Exception::class])
    fun signUp(user: User) {
        Assert.isTrue(db.users.add(user.apply {
            uuid = UUID.randomUUID().toString()
            password = password.encodeBCrypt()
            joinAt = LocalDateTime.now()
            signUpAt = joinAt
        }) > 0, "failed to add a user")

        Assert.isTrue(db.userRoles.add(UserRole().apply {
            userUUID = user.uuid
            role = Role().apply { id = RoleRepository.IDEnum.USER }
        }) > 0, "failed to assign role")
    }
}
