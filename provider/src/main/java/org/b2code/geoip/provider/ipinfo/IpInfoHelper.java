package org.b2code.geoip.provider.ipinfo;

import io.ipinfo.api.model.IPResponse;
import lombok.experimental.UtilityClass;
import org.b2code.geoip.persistence.entity.GeoIpInfo;

@UtilityClass
public class IpInfoHelper {

    GeoIpInfo map(IPResponse ipInfoResponse, String ipAddress) {
        return GeoIpInfo.builder()
                .ip(ipAddress)
                .city(ipInfoResponse.getCity())
                .postalCode(ipInfoResponse.getPostal())
                .country(ipInfoResponse.getCountryName())
                .countryIsoCode(ipInfoResponse.getCountryCode())
                .continent(ipInfoResponse.getContinent().getName())
                .latitude(Double.valueOf(ipInfoResponse.getLatitude()))
                .longitude(Double.valueOf(ipInfoResponse.getLongitude()))
                .build();
    }
}
