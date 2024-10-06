package org.b2code.geoip.database;

import jakarta.validation.constraints.NotNull;
import org.b2code.geoip.GeoIpInfo;
import org.keycloak.provider.Provider;

public interface GeoipDatabaseAccessProvider extends Provider {

    @NotNull
    GeoIpInfo getIpInfo(@NotNull String ipAddress);
}
