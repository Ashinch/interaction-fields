package com.interactionfields.signaling

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients

@EnableDiscoveryClient
@EnableFeignClients(basePackages = ["com.interactionfields"])
@SpringBootApplication(scanBasePackages = ["com.interactionfields"])
class ServiceSignalingApplication

fun main(args: Array<String>) {
    runApplication<ServiceSignalingApplication>(*args)
}
