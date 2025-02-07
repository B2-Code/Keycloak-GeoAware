package org.b2code.geoip.cache;

import jakarta.validation.constraints.NotNull;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.geoip.GeoIpInfo;
import org.b2code.geoip.GeoIpProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.tracing.TracingProvider;
import org.keycloak.tracing.TracingProviderUtil;

import java.util.Optional;

@JBossLog
public abstract class CachingGeoIpProvider implements GeoIpProvider {

    private final KeycloakSession session;
    private final GeoIpCacheProvider cacheProvider;
    private final TracingProvider tracingProvider;

    public CachingGeoIpProvider(KeycloakSession session) {
        log.debugf("Creating %s", CachingGeoIpProvider.class.getSimpleName());
        this.session = session;
        this.cacheProvider = session.getProvider(GeoIpCacheProvider.class);
        this.tracingProvider = TracingProviderUtil.getTracingProvider(session);
    }

    public final GeoIpInfo getIpInfo(String ip) {
        return tracingProvider.trace(CachingGeoIpProvider.class, "getIpInfo", span -> {
            return getTraced(ip);
        });
    }

    private GeoIpInfo getTraced(String ip) {
        Optional<GeoIpInfo> cachedGeoIpInfo = cacheProvider.get(session, ip);
        tracingProvider.getCurrentSpan().setAttribute("cache.hit", cachedGeoIpInfo.isPresent());
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
