package com.interactionfields.signaling

import com.interactionfields.common.response.R
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {

    @GetMapping("/ssl")
    fun ssl(): R = R.success("ssl")
}
