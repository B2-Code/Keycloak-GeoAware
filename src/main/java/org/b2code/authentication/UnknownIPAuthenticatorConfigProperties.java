package org.b2code.authentication;

import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;

import java.util.List;

public class UnknownIPAuthenticatorConfigProperties {

    public static final ProviderConfigProperty EMAIL_MODUS_PROPERTY = new ProviderConfigProperty(
            UnknownIPAuthenticatorConfig.EMAIL_MODUS,
            "E-Mail Modus",
            "The modus of the authenticator",
            ProviderConfigProperty.LIST_TYPE,
            null,
            false);

    static {
        EMAIL_MODUS_PROPERTY.setOptions(List.of("ALWAYS", "ON_CHANGE"));
    }

    static final List<ProviderConfigProperty> CONFIG_PROPERTIES = ProviderConfigurationBuilder.create()
            .property(EMAIL_MODUS_PROPERTY)
            .build();

    private UnknownIPAuthenticatorConfigProperties() {
    }
}
