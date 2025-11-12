package org.igdevx.shopservice.integration.elasticsearch;

import org.igdevx.shopservice.IntegrationTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Base test class for Elasticsearch integration tests
 * Provides Testcontainers setup for PostgreSQL and Elasticsearch
 * Uses singleton pattern to reuse containers across all tests
 */
@IntegrationTest
@Transactional
public abstract class ElasticsearchIntegrationTestBase {

    // Singleton containers - started once for all tests
    private static final PostgreSQLContainer<?> postgreSQLContainer;
    private static final ElasticsearchContainer elasticsearchContainer;

    static {
        postgreSQLContainer = new PostgreSQLContainer<>(
                DockerImageName.parse("postgres:15-alpine")
        )
                .withDatabaseName("shop_service_test")
                .withUsername("test")
                .withPassword("test");

        elasticsearchContainer = new ElasticsearchContainer(
                DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:8.11.0")
                        .asCompatibleSubstituteFor("docker.elastic.co/elasticsearch/elasticsearch")
        )
                .withEnv("xpack.security.enabled", "false")
                .withEnv("discovery.type", "single-node")
                .withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx512m")
                .withEnv("cluster.routing.allocation.disk.threshold_enabled", "false");

        // Start containers
        postgreSQLContainer.start();
        elasticsearchContainer.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // PostgreSQL - Testcontainers will auto-start the container
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

        // Elasticsearch - Testcontainers will auto-start the container
        registry.add("spring.elasticsearch.uris", () -> "http://" + elasticsearchContainer.getHttpHostAddress());
        registry.add("spring.elasticsearch.connection-timeout", () -> "10s");
        registry.add("spring.elasticsearch.socket-timeout", () -> "30s");

        // Flyway
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.flyway.locations", () -> "classpath:db/migration,classpath:db/test");

        // Disable external services
        registry.add("eureka.client.enabled", () -> "false");
        registry.add("spring.cloud.config.enabled", () -> "false");
    }
}

