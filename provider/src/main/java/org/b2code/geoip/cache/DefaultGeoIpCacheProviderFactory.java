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


    private static final int DEFAULT_MAX_CACHE_ENTRIES = 25;
    private static final String MAX_CACHE_ENTRIES_CONFIG_PARM = "max.cache.entries";

    private static final int DEFAULT_EXPIRE_AFTER_ACCESS_HOURS = 6;
    private static final String EXPIRE_AFTER_ACCESS_HOURS_CONFIG_PARM = "max.cache.hours";

    private Config.Scope config;
    private DefaultGeoIpCacheProvider instance;


    @Override
    public DefaultGeoIpCacheProvider create(KeycloakSession keycloakSession) {
        return instance;
    }

    @Override
    public void init(Config.Scope scope) {
        this.config = scope;
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
        int maxCacheEntries = config.getInt(MAX_CACHE_ENTRIES_CONFIG_PARM, DEFAULT_MAX_CACHE_ENTRIES);
        int expireAfterAccessHours = config.getInt(EXPIRE_AFTER_ACCESS_HOURS_CONFIG_PARM, DEFAULT_EXPIRE_AFTER_ACCESS_HOURS);
        instance = new DefaultGeoIpCacheProvider(maxCacheEntries, expireAfterAccessHours);
        log.infof("Configured GeoIP cache with max entries: %d, expire after access hours: %d", maxCacheEntries, expireAfterAccessHours);
    }

    @Override
    public void close() {
        instance.invalidateAll();
    }

    @Override
    public String getId() {
        return "default";
    }
}
