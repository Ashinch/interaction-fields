plugins {
    java
    kotlin("jvm") version "1.5.21"
    id("org.jetbrains.kotlin.plugin.spring") version "1.5.21"
    id("org.springframework.boot") version "2.5.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

group = "com.interactionfields"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
//        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2020.0.3")
        mavenBom("com.alibaba.cloud:spring-cloud-alibaba-dependencies:2021.1")
    }
}

dependencies {
    // Module
    implementation(project(":if-common"))

    // Feign
    api("org.springframework.cloud:spring-cloud-starter-openfeign")
    api("com.alibaba.cloud:spring-cloud-starter-alibaba-sentinel")
    api("org.springframework.cloud:spring-cloud-starter-loadbalancer")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    enabled = false
}
tasks.withType<Jar> {
    enabled = true
}
