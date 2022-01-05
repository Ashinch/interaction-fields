package com.interactionfields.auth.common.userdetails

import com.interactionfields.auth.common.util.BCryptPasswordEncoderExt.matchesBCrypt
import com.interactionfields.auth.common.util.JWTExt.getTokenSignature
import com.interactionfields.auth.common.util.clientAuthorization
import com.interactionfields.auth.common.util.contextAuthToken
import com.interactionfields.common.domain.User
import com.interactionfields.common.extension.JsonExt.toObj
import com.interactionfields.common.extension.ObjectExt.copyFrom
import com.interactionfields.common.repository.UserRepository.users
import com.interactionfields.common.repository.UserRoleRepository.userRoles
import com.interactionfields.common.response.C
import mu.KotlinLogging
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.filter
import org.ktorm.entity.find
import org.ktorm.entity.map
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.jwt.JwtHelper
import org.springframework.stereotype.Service
import org.springframework.util.Assert
import java.time.LocalDateTime
import java.util.*

/**
 * Implement Spring Security's [UserDetailsService] interface.
 *
 * @author Ashinch
 * @date 2021/08/25
 */
@Service
class UserDetailsService(
    private val db: Database,
    private val authServiceRPC: AuthServiceRPC,
    private val redisTemplate: StringRedisTemplate
) : UserDetailsService {

    private val logger = KotlinLogging.logger {}

    /**
     * Load the user by [username].
     */
    override fun loadUserByUsername(username: String?): UserDetails {
        val user = loadUserByUsernameInternal(username)
        // Setting UserDetails authorities
        return UserDetails().copyFrom(user).apply {
            setAuthorities(db.userRoles
                .filter { it.userUUID eq user.uuid }
                .map { SimpleGrantedAuthority(it.role.name) })
        }
    }

    /**
     * Login using the [username] and [password].
     */
    fun login(username: String, password: String): UserLoginVO {
        val user = loadUserByUsernameInternal(username)
        if (!password.matchesBCrypt(user.password)) throw BadCredentialsException(C.BAD_CREDENTIALS.msg)
        // Get the JWT token from the authentication server
        val jwt = authServiceRPC.getToken("Basic $clientAuthorization", "password", username, password)
            ?: throw BadCredentialsException(C.BAD_CREDENTIALS.msg)
        // Put JWT's signature into Redis
        addSignatureInternal(username, jwt.access_token)
        // Refresh login date
        user.joinAt = LocalDateTime.now()
        user.flushChanges()
        // Setting UserDetailsDTO authorities
        return UserLoginVO(UserDetailsDTO().copyFrom(user).apply {
            authorities = db.userRoles
                .filter { it.userUUID eq user.uuid }
                .map { it.role.name }
        }, jwt)
    }

    fun refreshToken(username: String, refreshToken: String): JWT {
        val signature = contextAuthToken!!.getTokenSignature()
        val jwt = authServiceRPC.refreshToken("Basic $clientAuthorization", refreshToken = refreshToken)
            ?: throw BadCredentialsException(C.BAD_CREDENTIALS.msg)
        contextAuthToken = jwt.access_token
        removeSignatureInternal(username, signature)
        addSignatureInternal(username, jwt.access_token)
        return jwt
    }

    fun logout(username: String) {
        removeSignatureInternal(username, contextAuthToken!!.getTokenSignature())
    }

    fun session(username: String): Set<String>? {
        return redisTemplate.boundSetOps("token.${username}").members()
    }

    fun offline(username: String, signature: String) {
        return removeSignatureInternal(username, signature)
    }

    private fun loadUserByUsernameInternal(username: String?): User =
        db.users.find { it.username eq (username ?: "") }
            ?: throw UsernameNotFoundException(C.USER_NOT_FOUND.msg)

    private fun removeSignatureInternal(username: String, signature: String) {
        redisTemplate.boundSetOps("token.$username").remove(signature)
    }

    private fun addSignatureInternal(username: String, accessToken: String) {
        redisTemplate.boundSetOps("token.$username").add(accessToken.getTokenSignature())
        val token: Token = JwtHelper.decode(accessToken).claims.toObj(Token::class.java) as Token
        Assert.notNull(token.exp, "Token's expiration time must not be null")
        redisTemplate.expireAt("token.$username", Date(token.exp!! * 1000L))
    }
}
