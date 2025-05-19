package com.infobip.fer.course.shortener.recordingservice;

import com.github.dockerjava.api.model.HealthCheck;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.junit.jupiter.api.AfterEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Sql("/clean_test_data.sql")
public class IntegrationTestBase {

    public static final String EXCHANGE_NAME = "x.redirect-events";
    public static final String QUEUE_NAME = "q.redirect-events";

    private static final String RABBIT_MQ_IMAGE = "rabbitmq:4.1-management-alpine";
    private static final HealthCheck RABBIT_MQ_HEALTHCHECK = new HealthCheck()
            .withTest(List.of("CMD", "rabbitmq-diagnostics", "-q", "ping"))
            .withInterval(Duration.ofSeconds(10).toNanos())
            .withRetries(10)
            .withTimeout(Duration.ofSeconds(30).toNanos());


    @Container
    @ServiceConnection
    protected static final RabbitMQContainer RABBIT_MQ = new RabbitMQContainer(DockerImageName.parse(RABBIT_MQ_IMAGE))
            // Očekujemo da redirect/service konfigurira exchange i queue za RabbitMQ.
            // Zato za integracijske testove recording/servicea unaprijed definiramo
            // RabbitMQ sa očekivanim queueom i exchangeom.
            .withCopyFileToContainer(MountableFile.forClasspathResource("rabbitmq.conf"), "/etc/rabbitmq/rabbitmq.conf")
            .withCopyFileToContainer(MountableFile.forClasspathResource("definitions.json"), "/etc/rabbitmq/definitions.json")
            .withCreateContainerCmdModifier(cmd -> cmd.withHealthcheck(RABBIT_MQ_HEALTHCHECK))
            .waitingFor(Wait.forHealthcheck());

    @AfterEach
    void purgeRabbitMqQueue() throws IOException, TimeoutException {
        try (var connection = getRabbitMqConnection()) {
            try (var channel = connection.createChannel()) {
                channel.queuePurge(QUEUE_NAME);
            }
        }
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
