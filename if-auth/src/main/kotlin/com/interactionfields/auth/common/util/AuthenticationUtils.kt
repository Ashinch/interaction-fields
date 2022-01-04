package com.interactionfields.auth.common.util

import com.interactionfields.auth.common.userdetails.UserDetails
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails
import java.util.*

/**
 * Top-level function for authentication.
 *
 * @author Ashinch
 * @date 2021/08/25
 */

val contextAuth: Authentication get() = SecurityContextHolder.getContext().authentication

val contextAuthId: Int get() = contextAuthPrincipal.getId()!!

val contextAuthDetails get() = contextAuth.details as OAuth2AuthenticationDetails

val contextAuthPrincipal get() = contextAuth.principal as UserDetails

val contextIsAuthenticated get() = contextAuth.isAuthenticated

val clientAuthorization: String get() = Base64.getEncoder().encodeToString("client:276364092".toByteArray())
