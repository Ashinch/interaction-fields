package com.interactionfields.auth.common.userdetails

/**
 * Data transfer object that the user login.
 *
 * @author Ashinch
 * @date 2021/08/25
 */
data class UserLoginDTO(
    var user: UserDetailsDTO,
    var jwt: JWT
)
