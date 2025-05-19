package com.infobip.fer.course.shortener.redirectservice;

import com.github.dockerjava.api.model.HealthCheck;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Sql("/setup_test_db.sql")
public abstract class IntegrationTestBase {

    public static final String QUEUE_NAME = "q.redirect-events";

    // verzije docker imagea odgovaraju onima iz docker-compose.yaml datoteke:
    private static final String REDIS_IMAGE = "redis/redis-stack-server:7.4.0-v3";
    private static final String RABBIT_MQ_IMAGE = "rabbitmq:4.1-management-alpine";

    // definicije healthcheckova odgovaraju onima iz docker-compose.yaml datoteke:
    public static final HealthCheck REDIS_HEALTHCHECK = new HealthCheck()
            .withTest(List.of("CMD", "redis-cli", "-a", "${URLS_CACHE_DEFAULT_PASSWORD}", "--raw", "incr", "ping"))
            .withInterval(Duration.ofSeconds(10).toNanos())
            .withRetries(10)
            .withTimeout(Duration.ofSeconds(30).toNanos());
    private static final HealthCheck RABBIT_MQ_HEALTHCHECK = new HealthCheck()
            .withTest(List.of("CMD", "rabbitmq-diagnostics", "-q", "ping"))
            .withInterval(Duration.ofSeconds(10).toNanos())
            .withRetries(10)
            .withTimeout(Duration.ofSeconds(30).toNanos());

    // Anotacija po kojoj testcontainers junit-jupiter biblioteka pronalazi  definiciju
    // docker imagea. Testcontainers će pokrenuti container zahvaljujući @Testcontainers
    // anotaciji na klasi.
    @Container
    // Anotacija po kojoj spring-boot-testcontainers biblioteka zna podesiti spring
    // beanove za rad s Redisom tako da se spoji na docker container koji će pokrenuti
    // testcontainers biblioteka. Zahvaljujući ovome nije potrebno navoditi parametre
    // u application-test.yaml datoteci, te nam omogućava korištenje nasumičnih portova.
    @ServiceConnection
    protected static final RedisContainer REDIS = new RedisContainer(DockerImageName.parse(REDIS_IMAGE))
            .withCreateContainerCmdModifier(cmd -> cmd.withHealthcheck(REDIS_HEALTHCHECK))
            .waitingFor(Wait.forHealthcheck());

    @Container
    @ServiceConnection
    protected static final RabbitMQContainer RABBIT_MQ = new RabbitMQContainer(DockerImageName.parse(RABBIT_MQ_IMAGE))
            .withCreateContainerCmdModifier(cmd -> cmd.withHealthcheck(RABBIT_MQ_HEALTHCHECK))
            .waitingFor(Wait.forHealthcheck());

    @Autowired
    private CacheManager cacheManager;

    /**
     * purgeRabbitMqQueue metoda osigurava da poruke koje jedan
     * test pošalje u RabbitMQ queue ne utječu na ostale testove.
     */
    @AfterEach
    void purgeRabbitMqQueue() throws IOException, TimeoutException {
        // Ovdje koristimo try with resources kako bi osigurali da se connection
        // i channel ispravno zatvore nakon korištenja, čak i u slučaju da queuePurge
        // metoda baci iznimku. Alternativno, mogli smo ovo napisati i kao:
        // Connection connection = null;
        // try {
        //     connection = getRabbitMqConnection();
        //     Channel channel = null;
        //     try {
        //         channel = connection.createChannel();
        //         channel.queuePurge(QUEUE_NAME);
        //     } finally {
        //         if (channel != null) {
        //             channel.close();
        //         }
        //     }
        // } finally {
        //     if (connection != null) {
        //         connection.close();
        //     }
        // }
        try (var connection = getRabbitMqConnection()) {
            try (var channel = connection.createChannel()) {
                channel.queuePurge(QUEUE_NAME);
            }
        }
    }

    /**
     * clearCaches metoda osigurava da se Spring cache isprazni nakon
     * svakog testa. Na taj način osiguravamo da podatci spremljeni u
     * cache tokom izvršavanja jednog testa ne utječu na ostale.
     */
    @AfterEach
    void clearCaches() {
        cacheManager.getCacheNames().forEach(cacheName -> {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        });
    }

    private static Connection getRabbitMqConnection() throws IOException, TimeoutException {
        var factory = new ConnectionFactory();
        factory.setHost(RABBIT_MQ.getHost());
        factory.setPort(RABBIT_MQ.getMappedPort(5672));
        factory.setUsername(RABBIT_MQ.getAdminUsername());
        factory.setPassword(RABBIT_MQ.getAdminPassword());
        return factory.newConnection();
    }

}
