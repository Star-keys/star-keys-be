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
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    implementation("io.github.openfeign.querydsl:querydsl-jpa:6.11")
    annotationProcessor("io.github.openfeign.querydsl:querydsl-apt:6.11:jakarta")

    // ES
    implementation("co.elastic.clients:elasticsearch-java")
    implementation("org.elasticsearch.client:elasticsearch-rest-client")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
