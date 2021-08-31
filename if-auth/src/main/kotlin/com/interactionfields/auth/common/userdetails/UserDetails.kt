package com.interactionfields.auth.common.userdetails

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/**
 * Implement Spring Security's [UserDetails] interface.
 *
 * @author Ashinch
 * @date 2021/08/25
 */
class UserDetails : UserDetails {

    private var id: Int? = null
    private var username: String? = null
    private var password: String? = null
    private var enable: Boolean = true
    private var authorities: List<GrantedAuthority>? = null

    override fun getAuthorities() = authorities

    override fun getPassword() = password

    override fun getUsername() = username

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    override fun isCredentialsNonExpired() = true

    override fun isEnabled() = enable

    fun getId() = this.id

    fun setId(id: Int) {
        this.id = id
    }

    fun setAuthorities(authorities: List<GrantedAuthority>) {
        this.authorities = authorities
    }

    fun getEnable() = this.enable

    fun setEnable(enable: Boolean) {
        this.enable = enable
    }

    fun setUsername(username: String) {
        this.username = username
    }

    fun setPassword(password: String) {
        this.password = password
    }
}
