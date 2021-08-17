package com.interactionfields.meeting

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients

@EnableDiscoveryClient
@EnableFeignClients(basePackages = ["com.interactionfields"])
@SpringBootApplication(scanBasePackages = ["com.interactionfields"])
class ServiceMeetingApplication

fun main(args: Array<String>) {
    runApplication<ServiceMeetingApplication>(*args)
}
