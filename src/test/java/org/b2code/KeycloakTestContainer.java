package org.b2code;

import dasniko.testcontainers.keycloak.ExtendableKeycloakContainer;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.time.Duration;
import java.util.List;

@Slf4j
public class KeycloakTestContainer extends ExtendableKeycloakContainer<KeycloakTestContainer> {

    private static final String KEYCLOAK_VERSION = System.getProperty("keycloak.version", "latest");
    private static final String IMAGE_PREFIX = "quay.io/keycloak/keycloak";
    private static final String COMPILED_JAR = "target/keycloak-geoaware-jar-with-dependencies.jar";

    public KeycloakTestContainer() {
        super(IMAGE_PREFIX + ":" + KEYCLOAK_VERSION);
        File jar = new File(COMPILED_JAR);
        if (!jar.exists()) {
            throw new IllegalStateException("Compiled jar not found: " + COMPILED_JAR);
        }
        this
                .withProviderLibsFrom(List.of(jar))
                .withRealmImportFiles("realms/test-realm.json", "realms/test-users-0.json")
                .withProviderClassesFrom()
                .withVerboseOutput()
                .withStartupTimeout(Duration.ofSeconds(120));
        log.info("Starting Keycloak container with image '{}'", this.getDockerImageName());
    }


}
