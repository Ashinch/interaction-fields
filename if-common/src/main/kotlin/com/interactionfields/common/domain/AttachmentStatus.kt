package com.interactionfields.common.domain

import org.ktorm.entity.Entity

/**
 * The [AttachmentStatus] domain model.
 *
 * @author Ashinch
 * @date 2021/09/16
 */
interface AttachmentStatus : Entity<AttachmentStatus> {

    companion object : Entity.Factory<AttachmentStatus>()

    val id: Int
    var name: String
}
