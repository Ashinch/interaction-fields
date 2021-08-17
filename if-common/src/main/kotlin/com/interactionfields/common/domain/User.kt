package com.interactionfields.common.domain

import org.ktorm.entity.Entity
import java.time.LocalDateTime

/**
 * The [User] domain model.
 *
 * @author Ashinch
 * @date 2021/07/22
 */
interface User : Domain, Entity<User> {
    companion object : Entity.Factory<User>()

    var username: String
    var mobile: String
    var email: String
    var password: String
    var joinedAt: LocalDateTime
    var loggedAt: LocalDateTime
}
