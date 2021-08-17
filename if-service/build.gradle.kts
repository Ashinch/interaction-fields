plugins {
    java
    kotlin("jvm") version "1.5.21"
    id("org.jetbrains.kotlin.plugin.spring") version "1.5.21"
    id("org.springframework.boot") version "2.5.3" apply false
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

group = "com.interactionfields"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

subprojects {
    apply {
        plugin("java")
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.jetbrains.kotlin.plugin.spring")
        plugin("org.springframework.boot")
        plugin("io.spring.dependency-management")
    }

    group = "com.interactionfields"
    version = "1.0-SNAPSHOT"
    java.sourceCompatibility = JavaVersion.VERSION_11

    repositories {
        mavenCentral()
    }

    dependencyManagement {
        imports {
            mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:2020.0.3")
            mavenBom("com.alibaba.cloud:spring-cloud-alibaba-dependencies:2021.1")
        }
    }

    dependencies {
        val implementation by configurations

        // Module
        implementation(project(":if-common"))
        implementation(project(":if-auth"))
        implementation(project(":if-rpc"))

        // Nacos
        implementation("com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-discovery")
        implementation("com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-config")
        implementation("org.springframework.cloud:spring-cloud-starter-bootstrap")

        // Sleuth and Zipkin
        implementation("org.springframework.cloud:spring-cloud-starter-sleuth")
        implementation("org.springframework.cloud:spring-cloud-starter-zipkin:2.2.8.RELEASE")
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
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
}

