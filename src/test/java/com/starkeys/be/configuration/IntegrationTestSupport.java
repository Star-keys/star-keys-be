package com.starkeys.be.configuration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@ActiveProfiles("test")
@Testcontainers
@SpringBootTest
public abstract class IntegrationTestSupport {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:latest");

    @Container
    private static final GenericContainer<?> mongoContainer =
            new GenericContainer<>("mongo:latest")
                    .withExposedPorts(27017);

    @Container
    private static final GenericContainer<?> esContainer =
            new GenericContainer<>("elasticsearch:8.10.4")
                    .withExposedPorts(9200)
                    .withEnv("discovery.type", "single-node")
                    .withEnv("xpack.security.enabled", "false")
                    .withEnv("ES_JAVA_OPTS", "-Xms256m -Xmx256m");

    @DynamicPropertySource
    private static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);

        registry.add("spring.data.mongodb.uri", () ->
                "mongodb://" + mongoContainer.getHost() + ":" + mongoContainer.getMappedPort(27017));

        registry.add("spring.elasticsearch.uris", () ->
                "http://" + esContainer.getHost() + ":" + esContainer.getMappedPort(9200));
    }
}

