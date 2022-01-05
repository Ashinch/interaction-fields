package com.interactionfields.common.response

/**
 * The enumeration class of the response code and response message.
 *
 * @param code The response code
 * @param msg The response message
 *
 * @author Ashinch
 * @date 2021/07/24
 */
enum class C(
    val code: Int,
    val msg: String,
) {

    /**
     * Normal
     */
    SUCCESS(200, "Success"),
    FAILURE(500, "Failure"),
    BAD_REQUEST(400, "Bad Request"),

    /**
     * Parameter (1001 - 1999)
     */
    NULL(1001, "数据为空"),

    /**
     * Authentication (2001 - 2999)
     */
    LOGIN_SUCCESS(200, "登录成功"),
    USER_NOT_LOGIN(2001, "未登录"),
    ACCESS_DENIED(2002, "权限不足"),
    USER_NOT_FOUND(2003, "用户不存在"),
    BAD_CREDENTIALS(2004, "用户名或密码错误"),
    ACCOUNT_LOCKED(2005, "账户已锁定"),
    BAD_CLIENT_CREDENTIALS(2005, "客户端凭证无效"), ;
}
