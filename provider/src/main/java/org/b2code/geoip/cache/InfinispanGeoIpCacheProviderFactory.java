package org.b2code.geoip.cache;

import com.google.auto.service.AutoService;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.PluginConstants;
import org.b2code.ServerInfoAwareFactory;
import org.b2code.geoip.GeoIpInfo;
import org.infinispan.Cache;
import org.keycloak.Config;
import org.keycloak.connections.infinispan.InfinispanConnectionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@JBossLog
@AutoService(GeoIpCacheProviderFactory.class)
public class InfinispanGeoIpCacheProviderFactory extends ServerInfoAwareFactory implements GeoIpCacheProviderFactory {

    private static final String DEFAULT_CACHE_NAME = PluginConstants.PLUGIN_NAME_LOWER_CASE + "-geoip";
    private static final String CACHE_NAME_CONFIG_PARM = "cache.name";

    private Config.Scope config;
    private Cache<String, GeoIpInfo> cache;

    @Override
    public InfinispanGeoIpCacheProvider create(KeycloakSession keycloakSession) {
        return new InfinispanGeoIpCacheProvider(cache);
    }

    @Override
    public void init(Config.Scope scope) {
        this.config = scope;
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
        KeycloakSession keycloakSession = keycloakSessionFactory.create();
        InfinispanConnectionProvider connectionProvider = keycloakSession.getProvider(InfinispanConnectionProvider.class);
        String cacheName = getCacheName();
        this.cache = connectionProvider.getCache(cacheName);
        log.infof("Using Infinispan cache '%s' for GeoIP info", cacheName);
        log.debugf("Using config: %s", cache.getCacheConfiguration().toString());
    }

    private String getCacheName() {
        return config.get(CACHE_NAME_CONFIG_PARM, DEFAULT_CACHE_NAME);
    }

    @Override
    public void close() {
        // NOOP
    }

    @Override
    public String getId() {
        return "infinispan";
    }
}
