package com.interactionfields.auth.common.resource

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity

/**
 * Enable method security authentication for the resource server.
 *
 * @author Ashinch
 * @date 2021/08/25
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
class GlobalMethodSecurityConfiguration
