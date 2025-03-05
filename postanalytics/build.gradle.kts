plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-library`

    alias(libs.plugins.springframework.boot)
    alias(libs.plugins.spring.dependency.management)
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.reactor)

    implementation(libs.springboot.starter.webflux)
    implementation(libs.springboot.starter.test)
    implementation(libs.reactor.test)
    implementation(libs.junit.platform.launcher)

    implementation(libs.springboot.security)

    implementation(libs.springboot.starter.data.r2dbc)
    implementation(libs.r2dbc.postgresql)
    implementation(libs.postgresql)

    implementation(libs.springboot.starter.quartz)

    implementation(project(":common"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}