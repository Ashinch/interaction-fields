package com.interactionfields.gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@EnableDiscoveryClient
@SpringBootApplication
class ServiceGatewayApplication

fun main(args: Array<String>) {
    runApplication<ServiceGatewayApplication>(*args)
}
