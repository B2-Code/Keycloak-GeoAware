package org.b2code.geoip.database.maxmind;

import com.google.common.base.Stopwatch;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.geoip.GeoIpInfo;
import org.b2code.geoip.database.GeoipDatabaseAccessProvider;

import java.io.IOException;
import java.net.InetAddress;

@RequiredArgsConstructor
@JBossLog
public class MaxmindDatabaseAccessProvider implements GeoipDatabaseAccessProvider {

    private final DatabaseReader reader;

    public GeoIpInfo getIpInfo(String ipAddress) {
        if (reader != null) {
            try {
                Stopwatch stopwatch = Stopwatch.createStarted();
                CityResponse maxmindInfo = this.reader.city(InetAddress.getByName(ipAddress));
                log.debugf("Maxmind GeopIP lookup took %s", stopwatch.stop());
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
        } else {
            log.error("Maxmind database reader is not initialized");
        }
        return GeoIpInfo.builder().ip(ipAddress).build();
    }

    @Override
    public void close() {
        // NOOP
    }
}
