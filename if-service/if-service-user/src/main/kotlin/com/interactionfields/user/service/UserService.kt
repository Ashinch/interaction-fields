package com.interactionfields.user.service

import com.interactionfields.auth.common.util.BCryptPasswordEncoderExt.encodeBCrypt
import com.interactionfields.auth.common.util.BCryptPasswordEncoderExt.matchesBCrypt
import com.interactionfields.auth.common.util.contextAuthPrincipal
import com.interactionfields.common.domain.Role
import com.interactionfields.common.domain.User
import com.interactionfields.common.domain.UserRole
import com.interactionfields.common.extension.ObjectExt.copyFrom
import com.interactionfields.common.repository.RoleRepository
import com.interactionfields.common.repository.UserRepository.users
import com.interactionfields.common.repository.UserRoleRepository.userRoles
import com.interactionfields.user.model.param.UserInfoParam
import com.interactionfields.user.model.vo.UserVo
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find
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
        // Check if the user exists
        Assert.isNull(db.users.find { it.username eq user.username }, "The username already exists")

        // Insert the user
        Assert.isTrue(db.users.add(user.apply {
            uuid = UUID.randomUUID().toString()
            password = password.encodeBCrypt()
            joinAt = LocalDateTime.now()
            signUpAt = joinAt
        }) > 0, "Failed to add a user")

        // Assign roles
        Assert.isTrue(db.userRoles.add(UserRole().apply {
            userUUID = user.uuid
            role = Role().apply { id = RoleRepository.IDEnum.USER }
        }) > 0, "Failed to assign roles")
    }

    fun info(userInfo: UserInfoParam): UserVo {
        val user = db.users.find { it.uuid eq contextAuthPrincipal.getUuid()!! }!!
        userInfo.name?.takeIf { it.isNotBlank() }?.let { user.name = it }
        userInfo.email.takeIf { it.isNotBlank() }?.let { user.email = it }
        user.flushChanges()
        return UserVo().copyFrom(user)
    }

    fun changePwd(old: String, new: String): Boolean {
        val user = db.users.find { it.uuid eq contextAuthPrincipal.getUuid()!! }!!
        Assert.isTrue(old.matchesBCrypt(user.password), "The old password is incorrect")
        user.password = new.encodeBCrypt()
        user.flushChanges()
        return true
    }
}
