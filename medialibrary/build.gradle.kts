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
    testImplementation(libs.reactor.test)
    testImplementation(libs.junit.platform.launcher)

    implementation(libs.springboot.security)

    implementation(libs.springboot.starter.data.r2dbc)
    implementation(libs.r2dbc.postgresql)
    runtimeOnly(libs.postgresql)

    implementation(platform(libs.aws.sdk.bom))
    implementation(libs.aws.sdk.s3)
    implementation(libs.aws.sdk.sso)
    implementation(libs.aws.sdk.ssooidc)

    implementation(project(":common"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}
