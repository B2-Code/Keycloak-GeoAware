package org.b2code.config;

import org.keycloak.common.Profile;
import org.keycloak.testframework.server.KeycloakServerConfig;
import org.keycloak.testframework.server.KeycloakServerConfigBuilder;

public class ServerConfig implements KeycloakServerConfig {

    @Override
    public KeycloakServerConfigBuilder configure(KeycloakServerConfigBuilder keycloakServerConfigBuilder) {
        return keycloakServerConfigBuilder
                .dependency("org.b2code", "keycloak-geoaware-provider")
                .features(Profile.Feature.DECLARATIVE_UI);
    }
}
