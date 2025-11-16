package org.b2code.geoip.provider.maxmind;

import com.maxmind.geoip2.GeoIp2Provider;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.geoip.persistence.entity.GeoIpInfo;
import org.b2code.geoip.cache.CachingGeoIpProvider;
import org.keycloak.models.KeycloakSession;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Optional;

@JBossLog
public class MaxmindProvider extends CachingGeoIpProvider {

    private final GeoIp2Provider geoIpProvider;

    protected MaxmindProvider(KeycloakSession session, GeoIp2Provider geoIpProvider) {
        super(session);
        if (geoIpProvider == null) {
            throw new RuntimeException("GeoIp2Provider is not initialized");
        }
        this.geoIpProvider = geoIpProvider;
    }

    @Override
    protected Optional<GeoIpInfo> getIpInfoImpl(String ipAddress) {
        if (null == geoIpProvider) {
            log.warn("Maxmind GeoIP provider not initialized");
            return Optional.empty();
        }

        InetAddress inetAddress = getInetAddress(ipAddress);
        try {
            CityResponse maxmindInfo = geoIpProvider.city(inetAddress);
            return Optional.ofNullable(MaxmindHelper.map(maxmindInfo, ipAddress));
        } catch (IOException e) {
            log.error("Error while performing GeoIP lookup", e);
        } catch (GeoIp2Exception e) {
            log.warnf("Failed to get GeoIP info: %s", e.getMessage());
        }

        return Optional.empty();
    }

}
