package org.b2code.authentication;

import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;

import java.util.List;

public class UnknownIPAuthenticatorConfigProperties {

    public static final ProviderConfigProperty MODUS_PROPERTY = new ProviderConfigProperty(
            UnknownIPAuthenticatorConfig.MODUS,
            "Modus",
            "The modus of the authenticator",
            ProviderConfigProperty.LIST_TYPE,
            null,
            false);

    static {
        MODUS_PROPERTY.setOptions(List.of("ALLOWED", "DENIED"));
    }

    static final List<ProviderConfigProperty> CONFIG_PROPERTIES = ProviderConfigurationBuilder.create()
            .property(MODUS_PROPERTY)
            .build();

    private UnknownIPAuthenticatorConfigProperties() {
    }
}
