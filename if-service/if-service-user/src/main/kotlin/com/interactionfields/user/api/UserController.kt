package com.interactionfields.user.api

import com.interactionfields.auth.common.userdetails.UserDetailsService
import com.interactionfields.auth.common.util.HasAuthority
import com.interactionfields.auth.common.util.contextAuthPrincipal
import com.interactionfields.common.domain.User
import com.interactionfields.common.extension.ObjectExt.copyFrom
import com.interactionfields.common.response.R
import com.interactionfields.user.model.param.ChangePwdParam
import com.interactionfields.user.model.param.UserInfoParam
import com.interactionfields.user.model.param.UserParam
import com.interactionfields.user.model.param.UserSignUpParam
import com.interactionfields.user.service.UserService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@Validated
@RestController
class UserController(
    private val userService: UserService,
    private val userDetailsService: UserDetailsService,
) {

    @PostMapping("/login")
    fun login(@Valid userParam: UserParam): R =
        R.judge(userDetailsService.login(userParam.username, userParam.password))

    @PostMapping("/signUp")
    fun signUp(@Valid userParam: UserSignUpParam): R {
        userService.signUp(User().copyFrom(userParam))
        return R.judge(userDetailsService.login(userParam.username, userParam.password))
    }

    @PostMapping("/refreshToken")
    @PreAuthorize(HasAuthority.USER)
    fun refreshToken(refreshToken: String): R {
        return R.judge(userDetailsService.refreshToken(contextAuthPrincipal.username!!, refreshToken))
    }

    @PostMapping("/logOut")
    @PreAuthorize(HasAuthority.USER)
    fun logout(): R =
        R.success(userDetailsService.logout(contextAuthPrincipal.username!!))

    @PostMapping("/session")
    @PreAuthorize(HasAuthority.USER)
    fun session(): R =
        R.judge(userDetailsService.session(contextAuthPrincipal.username!!))

    @PostMapping("/offline")
    @PreAuthorize(HasAuthority.USER)
    fun offline(signature: String): R =
        R.success(userDetailsService.offline(contextAuthPrincipal.username!!, signature))

    @PostMapping("/edit")
    @PreAuthorize(HasAuthority.USER)
    fun edit(@Valid userInfo: UserInfoParam): R =
        R.success(userService.info(userInfo))

    @PostMapping("/changePwd")
    @PreAuthorize(HasAuthority.USER)
    fun changePwd(@Valid changePwdParam: ChangePwdParam): R =
        R.success(userService.changePwd(changePwdParam.old, changePwdParam.new))
}
