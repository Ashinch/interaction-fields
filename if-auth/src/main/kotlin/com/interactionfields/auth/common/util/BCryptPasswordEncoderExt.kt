package com.interactionfields.auth.common.util

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

/**
 * Utilities about the BCryptPasswordEncoder.
 *
 * @author Ashinch
 * @date 2021/08/20
 */
object BCryptPasswordEncoderExt {
    private val encoder = BCryptPasswordEncoder()

    /**
     * Use the BCrypt strong hash function to encode.
     */
    fun String.encodeBCrypt(): String = encoder.encode(this)

    /**
     * Use the BCrypt strong hash function to match.
     */
    fun CharSequence.matchesBCrypt(target: String): Boolean = encoder.matches(this, target)
}
