plugins {
	java

	alias(libs.plugins.springframework.boot)
	alias(libs.plugins.spring.dependency.management)
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

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
	implementation(libs.aws.sdk.s3)
	implementation(libs.aws.sdk.sso)
	implementation(libs.aws.sdk.ssooidc)

	implementation(libs.springboot.starter.data.r2dbc)

	runtimeOnly(libs.postgresql)

	implementation(libs.jackson.datatype.jsr310)

	implementation(project(":authentication"))
	implementation(project(":platformconnection"))
	implementation(project(":post"))
	implementation(project(":common"))
	implementation(project(":notification"))
	implementation(project(":postanalytics"))
	implementation(project(":medialibrary"))
}

tasks.withType<Test> {
	useJUnitPlatform()
}
