package com.interactionfields.common.repository

import com.interactionfields.common.domain.Role
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

/**
 * The mapper of database for [Role] domain.
 *
 * @author Ashinch
 * @date 2021/07/22
 */
object RoleRepository : Table<Role>("dict_role") {

    val id = int("id").primaryKey().bindTo { it.id }
    val name = varchar("name").bindTo { it.name }

    /**
     * Return a default entity sequence of [RoleRepository].
     */
    val Database.roles get() = this.sequenceOf(RoleRepository)

    object IDEnum {

        const val USER = 1
    }
}
