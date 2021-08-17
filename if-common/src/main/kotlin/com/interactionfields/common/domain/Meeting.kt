package com.interactionfields.common.domain

import org.ktorm.entity.Entity
import java.time.LocalDateTime

/**
 * The [Meeting] domain model.
 *
 * @author Ashinch
 * @date 2021/07/22
 */
interface Meeting : Domain, Entity<Meeting> {
    companion object : Entity.Factory<Meeting>()

    var creatorUUID: String
    var createdAt: LocalDateTime
    var endedAt: LocalDateTime
}
