plugins {
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
    implementation(libs.springboot.starter.webflux)
    testImplementation(libs.springboot.starter.test)
    testImplementation(libs.reactor.test)
    testRuntimeOnly(libs.junit.platform.launcher)

    implementation(libs.springboot.security)
    implementation(libs.nimbus.jose.jwt)

    implementation(platform(libs.aws.sdk.bom))
    implementation(libs.aws.sdk.cognito.identity.provider)

    implementation(project(":common"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}
