package org.b2code.geoip.maxmind;

import com.google.common.base.Stopwatch;
import com.maxmind.geoip2.GeoIp2Provider;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.geoip.cache.CachingGeoIpProvider;
import org.b2code.geoip.GeoIpInfo;
import org.keycloak.models.KeycloakSession;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

@JBossLog
public abstract class MaxmindProvider extends CachingGeoIpProvider {

    private final GeoIp2Provider geoIpProvider;

    protected MaxmindProvider(KeycloakSession session, GeoIp2Provider geoIpProvider) {
        super(session);
        this.geoIpProvider = geoIpProvider;
    }

    @Override
    protected GeoIpInfo getIpInfoImpl(String ipAddress) {
        if (null == geoIpProvider) {
            log.warn("Maxmind GeoIP provider not initialized");
            return GeoIpInfo.builder().ip(ipAddress).build();
        }

        InetAddress inetAddress = getInetAddress(ipAddress);
        if (null == inetAddress || inetAddress.isSiteLocalAddress() || inetAddress.isLoopbackAddress()) {
            log.debugf("Skipping GeoIP lookup for IP address '%s'", ipAddress);
            return GeoIpInfo.builder().ip(ipAddress).build();
        }

        try {
            Stopwatch stopwatch = Stopwatch.createStarted();
            CityResponse maxmindInfo = geoIpProvider.city(inetAddress);
            log.debugf("Maxmind GeoIP lookup took %s", stopwatch.stop());
            return MaxmindHelper.map(maxmindInfo, ipAddress);
        } catch (IOException e) {
            log.error("Error while performing GeoIP lookup", e);
        } catch (GeoIp2Exception e) {
            log.warnf("Failed to get GeoIP info: %s", e.getMessage());
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


    @Override
    public void close() {
        // NOOP
    }
}
