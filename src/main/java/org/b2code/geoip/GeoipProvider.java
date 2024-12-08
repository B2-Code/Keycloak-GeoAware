package org.b2code.geoip;

import jakarta.validation.constraints.NotNull;
import org.keycloak.provider.Provider;

public interface GeoipProvider extends Provider {

    @NotNull
    GeoIpInfo getIpInfo(@NotNull String ipAddress);
}
