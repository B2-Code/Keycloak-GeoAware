package org.b2code;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public abstract class IntegrationTestBase {

    @Container
    protected final KeycloakTestContainer keycloak = KeycloakTestContainer.create();

    /**
     * This method checks if the Keycloak test container is running and ready to accept requests.
     */
    @BeforeEach
    @Test
    protected void isTestContainerRunning() {
        assertTrue(keycloak.isRunning());
    }
}
