package com.interactionfields.user.api

import com.interactionfields.auth.common.userdetails.UserDetailsService
import com.interactionfields.auth.common.util.HasAuthority
import com.interactionfields.common.domain.User
import com.interactionfields.common.extension.ObjectExt.copyFrom
import com.interactionfields.common.response.R
import com.interactionfields.rpc.provider.MeetingServiceRPC
import com.interactionfields.user.model.param.UserParam
import com.interactionfields.user.service.UserService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@Validated
@RestController
@RequestMapping
class UserController(
    private val userService: UserService,
    private val userDetailsService: UserDetailsService,
    private val meetingServiceRPC: MeetingServiceRPC
) {
    @PostMapping("/login")
    fun login(@Valid userParam: UserParam): R =
        R.judge(userDetailsService.login(userParam.username, userParam.password))

    @PostMapping("/signUp")
    fun signUp(@Valid userParam: UserParam): R =
        R.judge(userService.signUp(User().copyFrom(userParam)), "insert error")

    @GetMapping("/test")
    @PreAuthorize(HasAuthority.USER)
    fun test(): R {
        return meetingServiceRPC.create()
//        return R.success()
    }
}
