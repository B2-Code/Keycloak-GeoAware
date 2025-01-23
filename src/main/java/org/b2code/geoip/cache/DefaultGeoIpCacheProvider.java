package org.b2code.geoip.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.admin.PluginConfigWrapper;
import org.b2code.geoip.GeoIpInfo;
import org.keycloak.models.KeycloakSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@JBossLog
@RequiredArgsConstructor
public class DefaultGeoIpCacheProvider implements GeoIpCacheProvider {

    private final Map<String, Cache<String, GeoIpInfo>> perRealmCache = new HashMap<>();

    private Cache<String, GeoIpInfo> createCache(KeycloakSession session) {
        PluginConfigWrapper config = new PluginConfigWrapper(session.getContext().getRealm());
        log.debugf("Creating cache with size %d", config.getGeoipDatabaseCacheSize());
        return CacheBuilder.newBuilder()
                .maximumSize(config.getGeoipDatabaseCacheSize())
                .build();
    }

    private Cache<String, GeoIpInfo> getOrCreateCache(KeycloakSession session) {
        String realmId = session.getContext().getRealm().getId();
        if (perRealmCache.containsKey(realmId)) {
            return perRealmCache.get(realmId);
        } else {
            log.debugf("Creating cache for realm '%s'", realmId);
            Cache<String, GeoIpInfo> cache = createCache(session);
            perRealmCache.put(realmId, cache);
            return cache;
        }
    }

    @Override
    public void put(KeycloakSession session, String ipAddress, GeoIpInfo geoIpInfo) {
        log.tracef("Putting '%s' into cache", ipAddress);
        getOrCreateCache(session).put(ipAddress, geoIpInfo);
    }

    @Override
    public Optional<GeoIpInfo> get(KeycloakSession session, String ipAddress) {
        if (!perRealmCache.containsKey(session.getContext().getRealm().getId())) {
            log.tracef("No cache for realm '%s'", session.getContext().getRealm().getId());
            return Optional.empty();
        } else {
            log.tracef("Getting '%s' from cache", ipAddress);
            return Optional.ofNullable(getOrCreateCache(session).getIfPresent(ipAddress));
        }
    }

    @Override
    public void close() {
        perRealmCache.values().forEach(Cache::invalidateAll);
    }
}