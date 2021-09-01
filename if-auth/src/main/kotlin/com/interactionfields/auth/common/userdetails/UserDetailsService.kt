package com.interactionfields.auth.common.userdetails

import com.interactionfields.auth.common.util.BCryptPasswordEncoderExt.matchesBCrypt
import com.interactionfields.common.domain.User
import com.interactionfields.common.extension.ObjectExt.copyFrom
import com.interactionfields.common.repository.RoleRepository
import com.interactionfields.common.repository.UserRepository.users
import com.interactionfields.common.repository.UserRoleRepository
import com.interactionfields.common.response.C
import mu.KotlinLogging
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.find
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.*

/**
 * Implement Spring Security's [UserDetailsService] interface.
 *
 * @author Ashinch
 * @date 2021/08/25
 */
@Service
class UserDetailsService(private val db: Database, private val authServiceRPC: AuthServiceRPC) : UserDetailsService {

    private val logger = KotlinLogging.logger {}

    /**
     * Load the user by [username].
     */
    override fun loadUserByUsername(username: String?): UserDetails {
        val user = loadUserByUsernameInternal(username)
        // Setting UserDetails authorities
        return UserDetails().copyFrom(user).apply {
            setAuthorities(db
                .from(RoleRepository)
                .leftJoin(UserRoleRepository, on = RoleRepository.id eq UserRoleRepository.roleID)
                .select(RoleRepository.name)
                .where(UserRoleRepository.userUUID eq user.uuid)
                .map { SimpleGrantedAuthority(it[RoleRepository.name]) })
        }
    }

    /**
     * Login using the [username] and [password].
     */
    fun login(username: String, password: String): UserLoginVO {
        val user = loadUserByUsernameInternal(username)
        if (!password.matchesBCrypt(user.password)) throw BadCredentialsException(C.BAD_CREDENTIALS.msg)
        // Get the JWT token from the authentication server
        val serviceId = "client:276364092"
        val base64Secret = Base64.getEncoder().encodeToString(serviceId.toByteArray())
        val jwt = authServiceRPC.getToken("Basic $base64Secret", "password", username, password)
            ?: throw BadCredentialsException(C.BAD_CREDENTIALS.msg)
        // Setting UserDetailsDTO authorities
        return UserLoginVO(UserDetailsDTO().copyFrom(user).apply {
            authorities = db
                .from(RoleRepository)
                .leftJoin(UserRoleRepository, on = RoleRepository.id eq UserRoleRepository.roleID)
                .select(RoleRepository.name)
                .where(UserRoleRepository.userUUID eq user.uuid)
                .map { it[RoleRepository.name].toString() }
        }, jwt)
    }

    private fun loadUserByUsernameInternal(username: String?): User =
        db.users.find { it.username eq (username ?: "") }
            ?: throw UsernameNotFoundException(C.USER_NOT_FOUND.msg)
}
