package org.b2code.geoip;

import jakarta.validation.constraints.NotNull;
import org.b2code.geoip.persistence.entity.GeoIpInfo;
import org.keycloak.provider.Provider;

public interface GeoIpProvider extends Provider {

    @NotNull
    GeoIpInfo getIpInfo(@NotNull String ipAddress);
}
