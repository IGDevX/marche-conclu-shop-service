package org.igdevx.shopservice;

import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark integration tests.
 * Integration tests verify that different components work correctly together.
 * They use Spring context and may use real or embedded external dependencies.
 *
 * Usage: @IntegrationTest
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Tag("integration")
@SpringBootTest
@ActiveProfiles("test")
public @interface IntegrationTest {
}

