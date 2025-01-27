package org.b2code.geoip.cache;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.geoip.GeoIpInfo;
import org.b2code.geoip.GeoIpProvider;
import org.keycloak.models.KeycloakSession;

import java.util.Optional;

@JBossLog
@RequiredArgsConstructor
public abstract class CachingGeoIpProvider implements GeoIpProvider {

    private final KeycloakSession session;

    public final GeoIpInfo getIpInfo(String ip) {
        GeoIpCacheProvider cacheProvider = session.getProvider(GeoIpCacheProvider.class);
        Optional<GeoIpInfo> cachedGeoIpInfo = cacheProvider.get(session, ip);
        if (cachedGeoIpInfo.isPresent()) {
            log.tracef("Cache hit for '%s'", ip);
            return cachedGeoIpInfo.get();
        }
        log.tracef("Cache miss for '%s'", ip);
        GeoIpInfo geoIpInfo = getIpInfoImpl(ip);
        cacheProvider.put(session, ip, geoIpInfo);
        return geoIpInfo;
    }

    protected abstract GeoIpInfo getIpInfoImpl(@NotNull String ipAddress);

}
