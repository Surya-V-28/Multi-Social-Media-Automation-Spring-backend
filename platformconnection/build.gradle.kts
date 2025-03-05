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

    implementation(libs.springboot.starter.quartz)

    implementation(libs.aws.sdk.bom)

    implementation(libs.springboot.starter.data.r2dbc)
    implementation(libs.r2dbc.postgresql)
    runtimeOnly(libs.postgresql)

    developmentOnly(libs.springboot.devtools)
    compileOnly(libs.project.lombok.lombok)
    annotationProcessor(libs.project.lombok.lombok)


    implementation(project(":common"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}
