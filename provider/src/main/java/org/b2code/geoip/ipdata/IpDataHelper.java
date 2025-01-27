package org.b2code.geoip.ipdata;

import io.ipdata.client.model.IpdataModel;
import lombok.experimental.UtilityClass;
import org.b2code.geoip.GeoIpInfo;

@UtilityClass
public class IpDataHelper {

    GeoIpInfo map(IpdataModel ipdataModel, String ipAddress) {
        return GeoIpInfo.builder()
                .ip(ipAddress)
                .city(ipdataModel.city())
                .postalCode(ipdataModel.postal())
                .country(ipdataModel.countryName())
                .countryIsoCode(ipdataModel.countryCode())
                .continent(ipdataModel.continentName())
                .latitude(ipdataModel.latitude())
                .longitude(ipdataModel.longitude())
                .build();
    }
}
