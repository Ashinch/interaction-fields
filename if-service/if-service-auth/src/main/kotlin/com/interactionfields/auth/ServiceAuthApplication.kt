package com.interactionfields.auth

import com.interactionfields.auth.common.resource.GlobalMethodSecurityConfiguration
import com.interactionfields.auth.common.resource.JWTConfiguration
import com.interactionfields.auth.common.resource.ResourceServerConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType

@EnableDiscoveryClient
@EnableFeignClients(basePackages = ["com.interactionfields"])
@SpringBootApplication
@ComponentScan(
    basePackages = ["com.interactionfields"],
    excludeFilters = [ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = [
            GlobalMethodSecurityConfiguration::class,
            JWTConfiguration::class,
            ResourceServerConfiguration::class
        ]
    )]
)
class ServiceAuthApplication

fun main(args: Array<String>) {
    runApplication<ServiceAuthApplication>(*args)
}
