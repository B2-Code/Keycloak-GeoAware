package org.b2code.geoip.cache;

import com.google.auto.service.AutoService;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.ServerInfoAwareFactory;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@JBossLog
@AutoService(GeoIpCacheProviderFactory.class)
public class DefaultGeoIpCacheProviderFactory extends ServerInfoAwareFactory implements GeoIpCacheProviderFactory {

    private static final String DURATION_CONFIG_KEY = "cacheDurationMinutes";
    private static final int DEFAULT_DURATION_DEFAULT = 60;

    private Config.Scope config;

    @Override
    public DefaultGeoIpCacheProvider create(KeycloakSession keycloakSession) {
        return new DefaultGeoIpCacheProvider(keycloakSession, getCacheDurationMinutes());
    }

    private int getCacheDurationMinutes() {
        return config.getInt(DURATION_CONFIG_KEY, DEFAULT_DURATION_DEFAULT);
    }

    @Override
    public void init(Config.Scope scope) {
        this.config = scope;
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // NOOP
    }

    @Override
    public void close() {
        // NOOP
    }

    @Override
    public String getId() {
        return "default";
    }
}
