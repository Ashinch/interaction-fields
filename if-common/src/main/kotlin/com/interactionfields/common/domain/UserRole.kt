package com.interactionfields.common.domain

import org.ktorm.entity.Entity

/**
 * The [UserRole] domain model.
 *
 * @author Ashinch
 * @date 2021/07/22
 */
interface UserRole : Entity<UserRole> {

    companion object : Entity.Factory<UserRole>()

    val id: Int
    var userUUID: String
    var roleID: Int
}
