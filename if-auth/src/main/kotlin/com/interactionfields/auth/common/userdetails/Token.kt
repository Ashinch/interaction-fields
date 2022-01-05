package com.interactionfields.auth.common.userdetails

/**
 * The Token bean.
 *
 * @author Ashinch
 * @date 2022/01/05
 */
data class Token(
    var exp: Long?= null,
    var user_name: String?= null,
    var authorities: List<String>?= null,
    var jti: String?= null,
    var client_id: String?= null,
    var scope: List<String>?= null,
)
