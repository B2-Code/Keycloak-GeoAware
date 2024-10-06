package org.b2code.geoip.database.maxmind;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.geoip.GeoIpInfo;
import org.b2code.geoip.database.GeoipDatabaseAccessProvider;

import java.io.IOException;
import java.net.InetAddress;

@JBossLog
public class MaxmindDatabaseAccessProvider implements GeoipDatabaseAccessProvider {

    private final DatabaseReader reader;

    public MaxmindDatabaseAccessProvider(DatabaseReader reader) {
        this.reader = reader;
    }

    public GeoIpInfo getIpInfo(String ipAddress) {
        if (reader == null) {
            log.error("Maxmind database reader is not initialized");
            return new GeoIpInfo();
        }
        try {
            CityResponse maxmindInfo = this.reader.city(InetAddress.getByName(ipAddress));
            return GeoIpInfo.builder()
                    .ip(ipAddress)
                    .city(maxmindInfo.getCity().getName())
                    .postalCode(maxmindInfo.getPostal().getCode())
                    .country(maxmindInfo.getCountry().getName())
                    .countryIsoCode(maxmindInfo.getCountry().getIsoCode())
                    .build();
        } catch (IOException e) {
            log.error("Error while reading Maxmind database file", e);
            return new GeoIpInfo();
        } catch (GeoIp2Exception e) {
            log.warnf("Failed to get GeoIP info: %s", e.getMessage());
            return GeoIpInfo.builder().ip(ipAddress).build();
        }
    }

    @Override
    public void close() {
        // NOOP
    }
}
