package org.b2code.geoip.provider.maxmind;

import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.CountryResponse;
import lombok.experimental.UtilityClass;
import org.b2code.geoip.persistence.entity.GeoIpInfo;

@UtilityClass
public class MaxmindHelper {

    GeoIpInfo map(CityResponse maxmindInfo, String ipAddress) {
        return GeoIpInfo.builder()
                .ip(ipAddress)
                .city(maxmindInfo.city().name())
                .postalCode(maxmindInfo.postal().code())
                .country(maxmindInfo.country().name())
                .countryIsoCode(maxmindInfo.country().isoCode())
                .continent(maxmindInfo.continent().name())
                .latitude(maxmindInfo.location().latitude())
                .longitude(maxmindInfo.location().longitude())
                .accuracyRadius(maxmindInfo.location().accuracyRadius())
                .build();
    }

    GeoIpInfo map(CountryResponse maxmindInfo, String ipAddress) {
        return GeoIpInfo.builder()
                .ip(ipAddress)
                .country(maxmindInfo.country().name())
                .countryIsoCode(maxmindInfo.country().isoCode())
                .continent(maxmindInfo.continent().name())
                .build();
    }
}
