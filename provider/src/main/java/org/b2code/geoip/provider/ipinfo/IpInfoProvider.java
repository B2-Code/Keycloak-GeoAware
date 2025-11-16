package org.b2code.geoip.provider.ipinfo;

import io.ipinfo.api.IPinfo;
import io.ipinfo.api.errors.RateLimitedException;
import io.ipinfo.api.model.IPResponse;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.geoip.persistence.entity.GeoIpInfo;
import org.b2code.geoip.cache.CachingGeoIpProvider;
import org.keycloak.models.KeycloakSession;

import java.util.Optional;

@JBossLog
public class IpInfoProvider extends CachingGeoIpProvider {

    private final IPinfo ipInfo;

    public IpInfoProvider(KeycloakSession session, IPinfo ipInfo) {
        super(session);
        this.ipInfo = ipInfo;
    }

    public Optional<GeoIpInfo> getIpInfoImpl(String ipAddress) {
        if (null == ipInfo) {
            log.warn("IpInfo GeoIP provider not initialized");
            return Optional.empty();
        }
        try {
            IPResponse ipInfoResponse = ipInfo.lookupIP(ipAddress);
            return Optional.ofNullable(IpInfoHelper.map(ipInfoResponse, ipAddress));
        } catch (RateLimitedException e) {
            log.error("Error while performing GeoIP lookup, because rate limit of IpInfo is reached", e);
        } catch (Exception e) {
            log.error("Error while performing GeoIP lookup", e);
        }

        return Optional.empty();
    }

}
