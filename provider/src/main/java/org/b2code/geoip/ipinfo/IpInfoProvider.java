package org.b2code.geoip.ipinfo;

import com.google.common.base.Stopwatch;
import io.ipinfo.api.IPinfo;
import io.ipinfo.api.errors.RateLimitedException;
import io.ipinfo.api.model.IPResponse;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.geoip.GeoIpInfo;
import org.b2code.geoip.cache.CachingGeoIpProvider;
import org.keycloak.models.KeycloakSession;

import java.net.InetAddress;
import java.net.UnknownHostException;

@JBossLog
public class IpInfoProvider extends CachingGeoIpProvider {

    private final IPinfo ipInfo;

    public IpInfoProvider(KeycloakSession session, IPinfo ipInfo) {
        super(session);
        this.ipInfo = ipInfo;
    }

    public GeoIpInfo getIpInfoImpl(String ipAddress) {
        if (null == ipInfo) {
            log.warn("IpInfo GeoIP provider not initialized");
            return GeoIpInfo.builder().ip(ipAddress).build();
        }

        InetAddress inetAddress = getInetAddress(ipAddress);
        if (null == inetAddress || inetAddress.isSiteLocalAddress() || inetAddress.isLoopbackAddress()) {
            log.debugf("Skipping GeoIP lookup for IP address '%s'", ipAddress);
            return GeoIpInfo.builder().ip(ipAddress).build();
        }

        try {
            Stopwatch stopwatch = Stopwatch.createStarted();
            IPResponse ipInfoResponse = ipInfo.lookupIP(ipAddress);
            log.debugf("IpInfo GeoIP lookup took %s", stopwatch.stop());
            return IpInfoHelper.map(ipInfoResponse, ipAddress);
        } catch (RateLimitedException e) {
            log.error("Error while performing GeoIP lookup, because rate limit of IpInfo is reached", e);
        } catch (Exception e) {
            log.error("Error while performing GeoIP lookup", e);
        }

        return GeoIpInfo.builder().ip(ipAddress).build();
    }

    private InetAddress getInetAddress(String ipAddress) {
        try {
            return InetAddress.getByName(ipAddress);
        } catch (UnknownHostException e) {
            log.errorf("Failed to resolve IP address '%s'", ipAddress, e);
            return null;
        }
    }

}
