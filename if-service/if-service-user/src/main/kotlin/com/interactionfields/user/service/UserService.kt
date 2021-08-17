package com.interactionfields.user.service

import com.interactionfields.common.domain.User
import com.interactionfields.common.repository.UserRepository.users
import org.ktorm.database.Database
import org.ktorm.entity.add
import org.springframework.stereotype.Service

@Service
class UserService(private val db: Database) {
    fun signUp(user: User): Boolean {
        return db.users.add(user) > 0
    }
}
