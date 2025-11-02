package org.b2code.geoip.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.geoip.persistence.entity.GeoIpInfo;
import org.keycloak.models.KeycloakSession;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@JBossLog
public class DefaultGeoIpCacheProvider implements GeoIpCacheProvider {

    private final Cache<String, GeoIpInfo> cache;

    public DefaultGeoIpCacheProvider(int cacheSize, int cacheHours) {
        log.debugf("Creating cache with size %d and expiration after %d hours", cacheSize, cacheHours);
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(cacheSize)
                .expireAfterWrite(cacheHours, TimeUnit.HOURS)
                .build();
    }

    @Override
    public void put(KeycloakSession session, String ipAddress, GeoIpInfo geoIpInfo) {
        log.tracef("Putting '%s' into cache", ipAddress);
        cache.put(ipAddress, geoIpInfo);
    }

    @Override
    public Optional<GeoIpInfo> get(KeycloakSession session, String ipAddress) {
        log.tracef("Getting '%s' from cache", ipAddress);
        return Optional.ofNullable(cache.getIfPresent(ipAddress));
    }

    protected void invalidateAll() {
        cache.invalidateAll();
    }

    @Override
    public void close() {
        // NOOP
    }
}