package org.b2code.geoip.maxmind;

import com.google.common.base.Stopwatch;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.geoip.GeoIpInfo;
import org.b2code.geoip.GeoipProvider;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

@RequiredArgsConstructor
@JBossLog
public class MaxmindFileProvider implements GeoipProvider {

    private final DatabaseReader reader;

    public GeoIpInfo getIpInfo(String ipAddress) {
        if (null == reader) {
            log.warn("Maxmind database reader is not initialized");
            return GeoIpInfo.builder().ip(ipAddress).build();
        }

        InetAddress inetAddress = getInetAddress(ipAddress);
        if (null == inetAddress || inetAddress.isSiteLocalAddress() || inetAddress.isLoopbackAddress()) {
            log.debugf("Skipping GeoIP lookup for IP address '%s'", ipAddress);
            return GeoIpInfo.builder().ip(ipAddress).build();
        }

        try {
            Stopwatch stopwatch = Stopwatch.createStarted();
            CityResponse maxmindInfo = reader.city(inetAddress);
            log.debugf("Maxmind GeoIP lookup took %s", stopwatch.stop());
            return GeoIpInfo.builder()
                    .ip(ipAddress)
                    .city(maxmindInfo.getCity().getName())
                    .postalCode(maxmindInfo.getPostal().getCode())
                    .country(maxmindInfo.getCountry().getName())
                    .countryIsoCode(maxmindInfo.getCountry().getIsoCode())
                    .continent(maxmindInfo.getContinent().getName())
                    .latitude(maxmindInfo.getLocation().getLatitude())
                    .longitude(maxmindInfo.getLocation().getLongitude())
                    .accuracyRadius(maxmindInfo.getLocation().getAccuracyRadius())
                    .build();
        } catch (IOException e) {
            log.error("Error while reading Maxmind database file", e);
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
