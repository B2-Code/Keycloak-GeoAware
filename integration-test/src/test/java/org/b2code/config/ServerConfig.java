package org.b2code.config;

import org.keycloak.testframework.server.KeycloakServerConfig;
import org.keycloak.testframework.server.KeycloakServerConfigBuilder;

import java.util.HashMap;
import java.util.Map;

public abstract class ServerConfig implements KeycloakServerConfig {

    @Override
    public KeycloakServerConfigBuilder configure(KeycloakServerConfigBuilder keycloakServerConfigBuilder) {
        Map<String, String> options = new HashMap<>();
        options.put("spi-geoaware-geoip-cache--default--max-cache-entries", "42");
        options.put("spi-geoaware-login-history--default--max-records", "5");
        options.put("spi-geoaware-geoip-cache--provider", "default");
        options.putAll(getOptions());
        String cacheConfigFile = cacheConfigFile();
        if (cacheConfigFile != null) {
            keycloakServerConfigBuilder.cacheConfigFile(cacheConfigFile);
            options.put("cache", "ispn");
        }
        return keycloakServerConfigBuilder
                .dependency("org.b2code", "keycloak-geoaware-provider")
                .options(options);
    }

    abstract Map<String, String> getOptions();

    String cacheConfigFile() {
        return null;
    }
}
