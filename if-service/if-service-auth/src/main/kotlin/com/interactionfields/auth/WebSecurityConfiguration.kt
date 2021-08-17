package com.interactionfields.auth

import com.interactionfields.auth.common.userdetails.UserDetailsService
import com.interactionfields.common.extension.WebExt.write
import com.interactionfields.common.response.C
import com.interactionfields.common.response.R
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * Spring Security configuration.
 *
 * @author Ashinch
 * @date 2021/08/20
 */
@Configuration
@EnableWebSecurity
class WebSecurityConfiguration(
    private val userDetailsService: UserDetailsService
) :
    WebSecurityConfigurerAdapter() {

    /**
     * Configure security authentication management.
     */
    @Bean
    override fun authenticationManagerBean(): AuthenticationManager = super.authenticationManagerBean()

    /**
     * Configuring the cipher encoder.
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService).passwordEncoder(BCryptPasswordEncoder())
    }

    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
            .exceptionHandling()
            .authenticationEntryPoint { _, response, _ -> response.write(R.with(C.USER_NOT_LOGIN)) }
            .and().authorizeRequests()
            .anyRequest().permitAll()
            .and().httpBasic()
            .and().exceptionHandling()
            .accessDeniedHandler { _, response, _ -> response.write(R.with(C.ACCESS_DENIED)) }
    }
}
