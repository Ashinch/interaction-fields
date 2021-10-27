package com.interactionfields.common.domain

import org.ktorm.entity.Entity

/**
 * The [AttachmentPlaceholder] domain model.
 *
 * @author Ashinch
 * @date 2021/10/22
 */
interface AttachmentPlaceholder : Entity<AttachmentPlaceholder> {

    companion object : Entity.Factory<AttachmentPlaceholder>()

    var id: Int
    var typeID: Int
    var placeholder: ByteArray
}
