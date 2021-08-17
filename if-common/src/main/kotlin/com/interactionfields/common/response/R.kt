package com.interactionfields.common.response

/**
 * The wrapper class of the response result.
 *
 * It's JSON look like this:
 * {
 *   "code": 200,
 *   "msg": "success",
 *   "data": null
 * }
 *
 * @param code The response code
 * @param msg The response message
 * @param data The response data
 *
 * @author Ashinch
 * @date 2021/07/24
 */
@Suppress("DataClassPrivateConstructor")
data class R private constructor(
    val code: Int?,
    var msg: String?,
    val data: Any?,
) {
    companion object {
        /**
         * Returns a [R] with [C] or [data] or [Exception].
         */
        fun with(c: C?, data: Any? = null, e: Exception? = null): R {
            e?.let { return R(C.ERROR.code, e.message, data) }
            return R(c?.code, c?.msg, data)
        }

        /**
         * Returns a success [R].
         */
        fun success(data: Any? = null) = with(C.SUCCESS, data)

        /**
         * Returns a failure [R].
         */
        fun failure(msg: String? = null, data: Any? = null): R {
            return with(C.ERROR, data).also { it.msg = msg ?: it.msg }
        }

        /**
         * If [data] parameter is true or it is not null, the success [R] is returned,
         * otherwise the failure [R] is returned.
         */
        fun judge(data: Any?, errorMsg: String? = null): R {
            data ?: return failure(errorMsg)
            return if (data is Boolean) {
                if (data) success(data) else failure(errorMsg)
            } else {
                success(data)
            }
        }
    }
}
