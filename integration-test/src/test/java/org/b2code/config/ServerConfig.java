package org.b2code.config;

import org.keycloak.testframework.server.KeycloakServerConfig;
import org.keycloak.testframework.server.KeycloakServerConfigBuilder;

import java.util.HashMap;
import java.util.Map;

public abstract class ServerConfig implements KeycloakServerConfig {

    @Override
    public KeycloakServerConfigBuilder configure(KeycloakServerConfigBuilder keycloakServerConfigBuilder) {
        Map<String, String> options = new HashMap<>(getOptions());
        return keycloakServerConfigBuilder
                .dependency("org.b2code", "keycloak-geoaware-provider")
                .options(options);
    }

    abstract Map<String, String> getOptions();

}
