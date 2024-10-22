package org.b2code.authentication;

import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;

import java.util.List;

public class UnknownIPAuthenticatorConfigProperties {

    public static final String ALWAYS = "Always";
    public static final String UNKNOWN_IP = "Unknown IP";
    public static final String ON_CHANGE = "On Change";

    public static final ProviderConfigProperty EMAIL_MODUS_PROPERTY = new ProviderConfigProperty(
            UnknownIPAuthenticatorConfig.EMAIL_MODUS,
            "E-mail notification modus",
            "Defines when an e-mail should be sent. Always: an e-mail is always sent. Unknown IP: An e-mail is only sent when the user has never logged in with this IP address. On Change: An e-mail is only sent when the user logs in with a different IP address compared to the last time.",
            ProviderConfigProperty.LIST_TYPE,
            ALWAYS,
            false);

    static {
        EMAIL_MODUS_PROPERTY.setOptions(List.of(ALWAYS, UNKNOWN_IP, ON_CHANGE));
    }

    static final List<ProviderConfigProperty> CONFIG_PROPERTIES = ProviderConfigurationBuilder.create()
            .property(EMAIL_MODUS_PROPERTY)
            .build();

    private UnknownIPAuthenticatorConfigProperties() {
    }
}
