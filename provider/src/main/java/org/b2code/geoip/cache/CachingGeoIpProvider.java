package org.b2code.geoip.cache;

import jakarta.validation.constraints.NotNull;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.geoip.persistence.entity.GeoIpInfo;
import org.b2code.geoip.provider.GeoIpProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.quarkus.runtime.Environment;
import org.keycloak.tracing.TracingProvider;
import org.keycloak.tracing.TracingProviderUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

@JBossLog
public abstract class CachingGeoIpProvider implements GeoIpProvider {

    private final KeycloakSession session;
    private final GeoIpCacheProvider cacheProvider;
    private final TracingProvider tracingProvider;

    protected CachingGeoIpProvider(KeycloakSession session) {
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
        if (!shouldLookupIp(ip)) {
            log.debugf("Skipping IP lookup for local or loopback address '%s'", ip);
            return getEmptyGeoIpInfo(ip);
        }
        Optional<GeoIpInfo> cachedGeoIpInfo = cacheProvider.get(session, ip);
        tracingProvider.getCurrentSpan().setAttribute("cache.hit", cachedGeoIpInfo.isPresent());
        if (cachedGeoIpInfo.isPresent()) {
            log.tracef("Cache hit for '%s'", ip);
            return cachedGeoIpInfo.get();
        }
        log.tracef("Cache miss for '%s'", ip);
        Optional<GeoIpInfo> geoIpInfo = getIpInfoImpl(ip);
        geoIpInfo.ifPresent(info -> cacheProvider.put(session, ip, info));
        return geoIpInfo.orElseGet(() -> getEmptyGeoIpInfo(ip));
    }

    private boolean shouldLookupIp(String ip) {
        if (Environment.isDevMode()) {
            return true;
        }
        InetAddress inetAddress = getInetAddress(ip);
        return null != inetAddress && !inetAddress.isSiteLocalAddress() && !inetAddress.isLoopbackAddress();
    }

    private GeoIpInfo getEmptyGeoIpInfo(String ip) {
        return GeoIpInfo.builder().ip(ip).build();
    }

    protected InetAddress getInetAddress(String ipAddress) {
        try {
            return InetAddress.getByName(ipAddress);
        } catch (UnknownHostException e) {
            log.errorf("Failed to resolve IP address '%s'", ipAddress, e);
            return null;
        }
    }

    protected abstract Optional<GeoIpInfo> getIpInfoImpl(@NotNull String ipAddress);

    @Override
    public void close() {
        // NOOP
    }

}
