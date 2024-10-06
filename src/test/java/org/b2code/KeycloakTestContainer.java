package org.b2code;

import dasniko.testcontainers.keycloak.ExtendableKeycloakContainer;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.output.Slf4jLogConsumer;

@Slf4j
public class KeycloakTestContainer extends ExtendableKeycloakContainer<KeycloakTestContainer> {

    public static final String TEST_REALM = "test";

    private static final String KEYCLOAK_VERSION = System.getProperty("keycloak.version", "latest");
    private static final String IMAGE_PREFIX = "quay.io/keycloak/keycloak";

    private KeycloakTestContainer() {
        super(IMAGE_PREFIX + ":" + KEYCLOAK_VERSION);
    }

    public static KeycloakTestContainer create() {
        try (KeycloakTestContainer container = new KeycloakTestContainer()) {
            return container
                    .withRealmImportFiles("realms/test-realm.json", "realms/test-users-0.json")
                    .withLogConsumer(new Slf4jLogConsumer(log))
                    .withDefaultProviderClasses();
        }
    }
}
