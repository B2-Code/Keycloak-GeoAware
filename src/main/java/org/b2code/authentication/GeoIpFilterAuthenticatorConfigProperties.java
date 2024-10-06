package org.b2code.authentication;

import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;

import java.util.List;

public class GeoIpFilterAuthenticatorConfigProperties {

    public static final ProviderConfigProperty ALLOWED_IP_RANGE_PROPERTY = new ProviderConfigProperty(
            GeoIpFilterAuthenticatorConfig.ALLOWED_IP_RANGE,
            "Allowed IP range",
            "The IP range that is allowed to access the application",
            ProviderConfigProperty.STRING_TYPE,
            null,
            false);
    static final List<ProviderConfigProperty> CONFIG_PROPERTIES = ProviderConfigurationBuilder.create()
            .property(ALLOWED_IP_RANGE_PROPERTY)
            .build();

    private GeoIpFilterAuthenticatorConfigProperties() {
    }

}
