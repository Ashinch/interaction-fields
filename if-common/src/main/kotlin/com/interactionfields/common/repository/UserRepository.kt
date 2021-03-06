package com.interactionfields.common.repository

import com.interactionfields.common.domain.User
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.varchar

/**
 * The mapper of database for [User] domain.
 *
 * @author Ashinch
 * @date 2021/07/22
 */
object UserRepository : Table<User>("tb_user") {

    val id = int("id").primaryKey().bindTo { it.id }
    val uuid = varchar("uuid").bindTo { it.uuid }
    val username = varchar("username").bindTo { it.username }
    val name = varchar("name").bindTo { it.name }
    val mobile = varchar("mobile").bindTo { it.mobile }
    val email = varchar("email").bindTo { it.email }
    val password = varchar("password").bindTo { it.password }
    val joinedAt = datetime("join_at").bindTo { it.joinAt }
    val signUpAt = datetime("sign_up_at").bindTo { it.signUpAt }

    /**
     * Return a default entity sequence of [UserRepository].
     */
    val Database.users get() = this.sequenceOf(UserRepository)
}
