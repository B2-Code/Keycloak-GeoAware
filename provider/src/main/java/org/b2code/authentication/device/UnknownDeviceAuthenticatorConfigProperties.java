package org.b2code.authentication.device;

import org.b2code.authentication.base.action.AuthenticatorActionConfigPropertyBuilder;
import org.b2code.authentication.base.action.AuthenticatorActionOption;
import org.b2code.authentication.base.condition.AuthenticatorConditionConfigPropertyBuilder;
import org.b2code.authentication.base.condition.AuthenticatorConditionOption;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;

import java.util.List;

public final class UnknownDeviceAuthenticatorConfigProperties {
    private static final List<AuthenticatorConditionOption> CONDITION_OPTIONS = List.of(AuthenticatorConditionOption.ALWAYS, AuthenticatorConditionOption.STRICT_ON_DEVICE_CHANGE, AuthenticatorConditionOption.STRICT_ON_UNKNOWN_DEVICE, AuthenticatorConditionOption.NEVER);
    private static final List<AuthenticatorActionOption> ACTION_OPTIONS = List.of(AuthenticatorActionOption.SEND_DEVICE_WARNING_EMAIL, AuthenticatorActionOption.DENY_ACCESS, AuthenticatorActionOption.LOG, AuthenticatorActionOption.DISABLE_USER);

    private static final ProviderConfigProperty CONDITION_PROPERTY = AuthenticatorConditionConfigPropertyBuilder.getChooseConditionConfigProperty(CONDITION_OPTIONS);
    private static final ProviderConfigProperty ACTION_PROPERTY = AuthenticatorActionConfigPropertyBuilder.getChooseActionConfigProperty(ACTION_OPTIONS);

    static final List<ProviderConfigProperty> CONFIG_PROPERTIES = ProviderConfigurationBuilder.create()
            .property(CONDITION_PROPERTY)
            .property(ACTION_PROPERTY)
            .build();

    private UnknownDeviceAuthenticatorConfigProperties() {
    }

}
