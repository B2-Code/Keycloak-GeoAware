package org.b2code;

import lombok.extern.jbosslog.JBossLog;
import org.junit.jupiter.api.BeforeEach;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@JBossLog
@Testcontainers
public abstract class IntegrationTestBase {

    public static final String TEST_REALM = "test";

    @Container
    protected KeycloakTestContainer keycloak = new KeycloakTestContainer();

    protected Keycloak client;
    protected RealmResource realm;

    @BeforeEach
    void beforeEach() {
        if (keycloak.isRunning()) {
            this.client = keycloak.getKeycloakAdminClient();
            this.realm = this.client.realm(TEST_REALM);
            log.info("Keycloak is running, beginning the test");
        } else {
            log.error("Keycloak is not running");
        }
    }

}
