package com.interactionfields.auth.common.userdetails

/**
 * The JWT bean.
 *
 * @author Ashinch
 * @date 2021/08/25
 */
data class JWT(
    var access_token: String,
    var token_type: String,
    var refresh_token: String,
    var expires_in: Int = 0,
    var scope: String,
    var jti: String,
)
