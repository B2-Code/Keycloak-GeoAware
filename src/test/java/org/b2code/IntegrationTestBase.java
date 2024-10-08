package org.b2code;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import lombok.extern.jbosslog.JBossLog;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.time.Duration;
import java.util.List;

@JBossLog
@Testcontainers
public abstract class IntegrationTestBase {

    public static final String TEST_REALM = "test";

    private static final String KEYCLOAK_VERSION = System.getProperty("keycloak.version", "latest");
    private static final String IMAGE_PREFIX = "quay.io/keycloak/keycloak";
    private static final String COMPILED_JAR = "target/keycloak-geoaware-jar-with-dependencies.jar";

    @Container
    protected KeycloakContainer keycloak = createContainer();

    private static KeycloakContainer createContainer() {
        File jar = new File(COMPILED_JAR);
        return new KeycloakContainer(IMAGE_PREFIX + ":" + KEYCLOAK_VERSION)
                .withProviderLibsFrom(List.of(jar))
                .withRealmImportFiles("realms/test-realm.json", "realms/test-users-0.json")
                .withProviderClassesFrom()
                .withStartupTimeout(Duration.ofSeconds(120));
    }

    @BeforeEach
    void beforeEach() {
        log.infof("Started Keycloak with image %s", keycloak.getDockerImageName());
    }

    @AfterEach
    void afterEach() {
        log.info("Test completed, stopping Keycloak container");
        log.info("---- Container logs ----");
        log.info(keycloak.getLogs());
    }

}
