package com.interactionfields.auth.common.util

/**
 * Utilities about the JWT.
 *
 * @author Ashinch
 * @date 2021/08/20
 */
object JWTExt {

    /**
     * Split the JWT token to get the signature content.
     */
    fun String.getTokenSignature(): String = this.split(".")[2]
}
