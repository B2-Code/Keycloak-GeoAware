package org.b2code.config;

import org.keycloak.common.Profile;
import org.keycloak.testframework.server.KeycloakServerConfig;
import org.keycloak.testframework.server.KeycloakServerConfigBuilder;

import java.util.HashMap;
import java.util.Map;

public abstract class ServerConfig implements KeycloakServerConfig {

    @Override
    public KeycloakServerConfigBuilder configure(KeycloakServerConfigBuilder keycloakServerConfigBuilder) {
        Map<String, String> options = new HashMap<>();
        options.put("spi-geoaware-global-enabled", "true");
        options.put("spi-geoaware-global-login-history-max-records", "5");
        options.putAll(getOptions());
        return keycloakServerConfigBuilder
                .dependency("org.b2code", "keycloak-geoaware-provider")
                .features(Profile.Feature.DECLARATIVE_UI)
                .options(options);
    }

    abstract Map<String, String> getOptions();
}
