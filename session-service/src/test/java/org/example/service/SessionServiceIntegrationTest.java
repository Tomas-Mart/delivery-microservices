package org.example.service;

import org.example.model.Session;
import org.example.model.SessionStatus;
import org.example.repository.SessionRepository;
import org.example.rest.dto.SessionDto;
import org.example.service.impl.SessionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
public class SessionServiceIntegrationTest {

    private static final DockerImageName POSTGRES_IMAGE = DockerImageName
            .parse("dockerhub.timeweb.cloud/library/postgres:15")
            .asCompatibleSubstituteFor("postgres");

    private static final DockerImageName REDIS_IMAGE = DockerImageName
            .parse("dockerhub.timeweb.cloud/library/redis:7-alpine")
            .asCompatibleSubstituteFor("redis");

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(POSTGRES_IMAGE)
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(REDIS_IMAGE)
            .withExposedPorts(6379);

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SessionRepository sessionRepository;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", () -> "test");
        registry.add("spring.datasource.password", () -> "test");
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379).toString());
    }

    @BeforeEach
    void setUp() {
        sessionRepository.deleteAll();
    }

    @Test
    void start_ShouldCreateSessionInDatabase() {
        // when
        SessionDto result = sessionService.start("test-courier");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();

        Optional<Session> saved = sessionRepository.findById(result.getId());
        assertThat(saved).isPresent();
        assertThat(saved.get().getCourierId()).isEqualTo("test-courier");
        assertThat(saved.get().getStatus()).isEqualTo(SessionStatus.ACTIVE);  // Исправлено: сравниваем ENUM с ENUM
    }

    @Test
    void getSessionById_ShouldReturnCachedResult() {
        // given - создаём смену
        SessionDto created = sessionService.start("test-courier-2");
        Long sessionId = created.getId();

        // when - первый раз (из БД)
        long startTime1 = System.currentTimeMillis();
        SessionDto result1 = sessionService.getSessionById(sessionId);
        long duration1 = System.currentTimeMillis() - startTime1;

        // when - второй раз (из кэша)
        long startTime2 = System.currentTimeMillis();
        SessionDto result2 = sessionService.getSessionById(sessionId);
        long duration2 = System.currentTimeMillis() - startTime2;

        // then
        assertThat(result1).isNotNull();
        assertThat(result2).isNotNull();
        assertThat(result1.getId()).isEqualTo(result2.getId());
        assertThat(result1.getCourierId()).isEqualTo(result2.getCourierId());
        assertThat(result1.getStatus()).isEqualTo(SessionStatus.ACTIVE);  // Исправлено
        assertThat(result2.getStatus()).isEqualTo(SessionStatus.ACTIVE);  // Исправлено

        // Проверяем, что второй запрос быстрее (из кэша)
        assertThat(duration2).isLessThan(duration1);
    }
}