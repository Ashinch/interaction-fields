package com.interactionfields.common.repository

import com.interactionfields.common.domain.UserRole
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

/**
 * The mapper of database for [UserRole] domain.
 *
 * @author Ashinch
 * @date 2021/07/22
 */
object UserRoleRepository : Table<UserRole>("tb_user_role") {

    val id = int("id").primaryKey().bindTo { it.id }
    val userUUID = varchar("user_uuid").bindTo { it.userUUID }
    val role = int("role_id").references(RoleRepository) { it.role }

    /**
     * Return a default entity sequence of [UserRoleRepository].
     */
    val Database.userRoles get() = this.sequenceOf(UserRoleRepository)
}
