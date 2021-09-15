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

dependencies {
//    api("io.pivotal.spring.cloud:spring-cloud-services-dependencies:Hoxton.SR9")

    // Rabbit MQ
    api("org.springframework.boot:spring-boot-starter-amqp")

    // Ktorm
    api("org.ktorm:ktorm-core:3.4.1")
    api("org.ktorm:ktorm-jackson:3.4.1")
    api("org.ktorm:ktorm-support-mysql:3.4.1")
    api("mysql:mysql-connector-java:8.0.13")

    // Logging
    api("io.github.microutils:kotlin-logging:2.0.10")

    // Jackson
    api("com.fasterxml.jackson.module:jackson-module-kotlin")

    // javax.xml.bind (necessary for Java 11+)
    api("javax.xml.bind:jaxb-api:2.3.1")
    api("com.sun.xml.bind:jaxb-impl:3.0.1")
    api("org.glassfish.jaxb:jaxb-runtime:3.0.2")
    api("javax.activation:activation:1.1.1")

    // Spring Boot
    api("org.springframework.boot:spring-boot-starter-jdbc")
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-validation")
    api("org.springframework.boot:spring-boot-starter-aop")
    api("org.springframework.boot:spring-boot-starter-test")

    // Kotlin
    api("org.jetbrains.kotlin:kotlin-reflect")
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Junit
    testApi("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
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
