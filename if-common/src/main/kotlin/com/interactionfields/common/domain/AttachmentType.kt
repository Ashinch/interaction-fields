package com.interactionfields.common.domain

import org.ktorm.entity.Entity

/**
 * The [AttachmentType] domain model.
 *
 * @author Ashinch
 * @date 2021/09/16
 */
interface AttachmentType : Entity<AttachmentType> {

    companion object : Entity.Factory<AttachmentType>()

    var id: Int
    var name: String
}
