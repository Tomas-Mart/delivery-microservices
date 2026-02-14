package org.example.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest
public class OrderServiceImplIntegrationTest {

    private static final DockerImageName POSTGRES_IMAGE = DockerImageName
            .parse("dockerhub.timeweb.cloud/library/postgres:15")
            .asCompatibleSubstituteFor("postgres");

    private static final DockerImageName KAFKA_IMAGE = DockerImageName
            .parse("dockerhub.timeweb.cloud/confluentinc/cp-kafka:7.5.0")
            .asCompatibleSubstituteFor("confluentinc/cp-kafka");

    private static final DockerImageName REDIS_IMAGE = DockerImageName
            .parse("dockerhub.timeweb.cloud/library/redis:7-alpine")
            .asCompatibleSubstituteFor("redis");

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(POSTGRES_IMAGE)
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Container
    static KafkaContainer kafka = new KafkaContainer(KAFKA_IMAGE);

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(REDIS_IMAGE)
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", () -> "test");
        registry.add("spring.datasource.password", () -> "test");
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379).toString());
    }

    @Test
    void testOrderDeliveredEventTriggersPayout() {
        // тест сквозного сценария
    }
}