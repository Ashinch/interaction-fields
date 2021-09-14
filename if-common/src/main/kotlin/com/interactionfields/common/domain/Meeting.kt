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

    var title: String
    var code: String
    var creatorUUID: String
    var createAt: LocalDateTime
    var endAt: LocalDateTime
}
