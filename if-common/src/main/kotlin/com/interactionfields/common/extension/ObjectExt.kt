package com.interactionfields.common.extension

import org.springframework.beans.BeanUtils

/**
 * Utilities about the [Any] Object.
 *
 * @author Ashinch
 * @date 2021/08/18
 */
object ObjectExt {
    /**
     * Create a new instance by [T].
     */
    inline fun <reified T : Any> new(): T =
        T::class.java.getDeclaredConstructor()
            .apply { isAccessible = true }.newInstance()

    /**
     * Create a new instance by [T] and initialization [params].
     */
    inline fun <reified T : Any> new(vararg params: Any): T =
        T::class.java.getDeclaredConstructor(*params.map { it::class.java }
            .toTypedArray())
            .apply { isAccessible = true }.newInstance(*params)

    /**
     * Copy the property values of the given [any] bean into the [T] bean.
     */
    inline fun <reified T : Any> T.copyFrom(any: Any): T =
        also { BeanUtils.copyProperties(any, this) }
}
