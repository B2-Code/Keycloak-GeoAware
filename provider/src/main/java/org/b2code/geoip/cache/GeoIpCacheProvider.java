package org.b2code.geoip.cache;

import jakarta.validation.constraints.NotNull;
import org.b2code.geoip.persistence.entity.GeoIpInfo;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.Provider;

import java.util.Optional;

public interface GeoIpCacheProvider extends Provider {

    void put(KeycloakSession session, @NotNull String ipAddress, @NotNull GeoIpInfo geoIpInfo);

    Optional<GeoIpInfo> get(@NotNull KeycloakSession session, @NotNull String ipAddress);
}
