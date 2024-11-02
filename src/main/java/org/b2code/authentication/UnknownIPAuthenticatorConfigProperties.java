package org.b2code.authentication;

import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;

import java.util.List;
import java.util.stream.Stream;

public class UnknownIPAuthenticatorConfigProperties {

    public static final NotificationMode EMAIL_MODUS_DEFAULT = NotificationMode.ALWAYS;
    public static final ProviderConfigProperty EMAIL_MODUS_PROPERTY = new ProviderConfigProperty(
            UnknownIPAuthenticatorConfig.EMAIL_MODUS,
            "E-mail notification modus",
            "Defines when an e-mail should be sent. Always: an e-mail is always sent. Unknown IP: An e-mail is only sent when the user has never logged in with this IP address. On Change: An e-mail is only sent when the user logs in with a different IP address compared to the last time.",
            ProviderConfigProperty.LIST_TYPE,
            EMAIL_MODUS_DEFAULT.getLabel(),
            false);

    static {
        EMAIL_MODUS_PROPERTY.setOptions(Stream.of(NotificationMode.values()).map(NotificationMode::getLabel).toList());
    }

    static final List<ProviderConfigProperty> CONFIG_PROPERTIES = ProviderConfigurationBuilder.create()
            .property(EMAIL_MODUS_PROPERTY)
            .build();

    private UnknownIPAuthenticatorConfigProperties() {
    }
}
