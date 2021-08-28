package com.interactionfields.user.api

import com.interactionfields.auth.common.userdetails.UserDetailsService
import com.interactionfields.common.domain.User
import com.interactionfields.common.extension.ObjectExt.copyFrom
import com.interactionfields.common.response.R
import com.interactionfields.user.model.param.UserSignUpParam
import com.interactionfields.user.service.UserService
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
    fun login(@Valid userParam: UserSignUpParam): R =
        R.judge(userDetailsService.login(userParam.username, userParam.password))

    @PostMapping("/signUp")
    fun signUp(@Valid userParam: UserSignUpParam): R =
        R.judge(userService.signUp(User().copyFrom(userParam)))
}
