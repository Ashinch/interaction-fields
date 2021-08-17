package com.interactionfields.common.extension

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.web.context.support.WebApplicationContextUtils
import javax.servlet.http.HttpServletRequest

/**
 * Utilities about the Spring.
 *
 * @author Ashinch
 * @date 2021/08/18
 */
class SpringExt : ApplicationContextAware {
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        Companion.applicationContext = applicationContext
    }

    companion object {
        /**
         * Get [ApplicationContext].
         */
        var applicationContext: ApplicationContext? = null
            private set

        /**
         * Get a Bean by [Class].
         */
        fun <T> getBean(clazz: Class<T>): T {
            return applicationContext!!.getBean(clazz)
        }

        /**
         * Get a Bean by [Class] and [HttpServletRequest].
         */
        fun <T> getBean(request: HttpServletRequest, clazz: Class<T>): T {
            val context = request.servletContext
            val ctx: ApplicationContext? = WebApplicationContextUtils.getWebApplicationContext(context)
            return ctx!!.getBean(clazz)
        }
    }
}
