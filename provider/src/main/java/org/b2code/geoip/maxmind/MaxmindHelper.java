package org.b2code.geoip.maxmind;

import com.maxmind.geoip2.model.CityResponse;
import lombok.experimental.UtilityClass;
import org.b2code.geoip.persistence.entity.GeoIpInfo;

@UtilityClass
public class MaxmindHelper {

    GeoIpInfo map(CityResponse maxmindInfo, String ipAddress) {
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
    }
}
