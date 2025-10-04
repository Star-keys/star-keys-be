plugins {
    java
    id("org.springframework.boot") version "3.5.7-SNAPSHOT"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.starkeys"
version = "0.0.1-SNAPSHOT"
description = "star-keys-be"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    runtimeOnly("org.postgresql:postgresql")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
