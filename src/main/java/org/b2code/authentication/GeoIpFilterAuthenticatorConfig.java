package org.b2code.authentication;

import org.keycloak.models.AuthenticatorConfigModel;

import java.util.Optional;

public class GeoIpFilterAuthenticatorConfig {

    public static final String ALLOWED_IP_RANGE = "allowedIpRange";

    private final AuthenticatorConfigModel authenticatorConfigModel;

    public GeoIpFilterAuthenticatorConfig(AuthenticatorConfigModel authenticatorConfigModel) {
        this.authenticatorConfigModel = authenticatorConfigModel;
    }

    public String allowedIpRange() {
        return Optional.ofNullable(authenticatorConfigModel)
                .map(it -> it.getConfig().get(GeoIpFilterAuthenticatorConfig.ALLOWED_IP_RANGE)).orElse(null);
    }

}
