package com.interactionfields.common.domain

import org.ktorm.entity.Entity

/**
 * The [Role] domain model.
 *
 * @author Ashinch
 * @date 2021/07/22
 */
interface Role : Domain, Entity<Role> {
    companion object : Entity.Factory<Role>()

    var name: String
}
