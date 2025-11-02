package org.b2code.geoip.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.geoip.persistence.entity.GeoIpInfo;
import org.infinispan.Cache;
import org.keycloak.models.KeycloakSession;

import java.util.Optional;

@RequiredArgsConstructor
@JBossLog
public class InfinispanGeoIpCacheProvider implements GeoIpCacheProvider {

    private final Cache<String, GeoIpInfo> cache;

    @Override
    public void put(KeycloakSession session, String ipAddress, GeoIpInfo geoIpInfo) {
        log.tracef("Putting '%s' into cache", ipAddress);
        cache.put(ipAddress, geoIpInfo);
    }

    @Override
    public Optional<GeoIpInfo> get(KeycloakSession session, String ipAddress) {
        log.tracef("Getting '%s' from cache", ipAddress);
        return Optional.ofNullable(cache.get(ipAddress));
    }


    @Override
    public void close() {

    }
}