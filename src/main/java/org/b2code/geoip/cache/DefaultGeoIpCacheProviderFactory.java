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

    @Override
    public DefaultGeoIpCacheProvider create(KeycloakSession keycloakSession) {
        return new DefaultGeoIpCacheProvider();
    }

    @Override
    public void init(Config.Scope scope) {
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return "default";
    }
}
