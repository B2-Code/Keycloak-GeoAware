package org.b2code.authentication;

import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;

import java.util.List;
import java.util.stream.Stream;

public class UnknownIPAuthenticatorConfigProperties {

    public static final UnknownIPAuthenticatorNotificationMode EMAIL_MODE_DEFAULT = UnknownIPAuthenticatorNotificationMode.NEVER;
    public static final ProviderConfigProperty EMAIL_MODE_PROPERTY = new ProviderConfigProperty(
            UnknownIPAuthenticatorConfig.EMAIL_MODE,
            "E-mail notification mode",
            "Defines when an e-mail should be sent. Never: An e-mail is never sent. Always: An e-mail is always sent. Unknown IP: An e-mail is only sent when the user has never logged in with this IP address. On Change: An e-mail is only sent when the user logs in with a different IP address compared to the last time. Unknown Location: An e-mail is only sent when the user has never logged in from this location.",
            ProviderConfigProperty.LIST_TYPE,
            EMAIL_MODE_DEFAULT.getLabel(),
            false,
            true);

    static {
        EMAIL_MODE_PROPERTY.setOptions(Stream.of(UnknownIPAuthenticatorNotificationMode.values()).map(UnknownIPAuthenticatorNotificationMode::getLabel).toList());
    }

    static final List<ProviderConfigProperty> CONFIG_PROPERTIES = ProviderConfigurationBuilder.create()
            .property(EMAIL_MODE_PROPERTY)
            .build();

    private UnknownIPAuthenticatorConfigProperties() {
    }
}
