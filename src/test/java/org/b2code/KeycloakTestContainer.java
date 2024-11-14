package org.b2code;

import dasniko.testcontainers.keycloak.ExtendableKeycloakContainer;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.utility.MountableFile;

import java.nio.file.Paths;
import java.time.Duration;

@Slf4j
public class KeycloakTestContainer extends ExtendableKeycloakContainer<KeycloakTestContainer> {

    private static final String KEYCLOAK_VERSION = System.getProperty("keycloak.version", "latest");
    private static final String IMAGE_PREFIX = "quay.io/keycloak/keycloak";
    private static final String COMPILED_JAR = "target/keycloak-geoaware-shaded.jar";
    private static final String TARGET_PATH = "/opt/keycloak/providers/keycloak-geoaware-shaded.jar";

    public KeycloakTestContainer() {
        super(IMAGE_PREFIX + ":" + KEYCLOAK_VERSION);
        this
                .withRealmImportFiles("realms/test-realm.json", "realms/test-users-0.json")
                .withProviderClassesFrom()
                .withVerboseOutput()
                .withStartupTimeout(Duration.ofSeconds(120))
                .withCopyFileToContainer(MountableFile.forHostPath(Paths.get(COMPILED_JAR).toAbsolutePath()), TARGET_PATH);
        log.info("Starting Keycloak container with image '{}'", this.getDockerImageName());
    }

}
