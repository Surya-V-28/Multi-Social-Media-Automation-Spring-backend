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

    developmentOnly(libs.springboot.devtools)
    compileOnly(libs.project.lombok.lombok)
    annotationProcessor(libs.project.lombok.lombok)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
