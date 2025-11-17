package org.b2code.authentication.ip;

import org.b2code.authentication.base.action.AuthenticatorActionConfigPropertyBuilder;
import org.b2code.authentication.base.action.AuthenticatorActionOption;
import org.b2code.authentication.base.condition.AuthenticatorConditionConfigPropertyBuilder;
import org.b2code.authentication.base.condition.AuthenticatorConditionOption;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;

import java.util.List;

public final class IpAuthenticatorConfigProperties {

    private static final List<AuthenticatorConditionOption> CONDITION_OPTIONS = List.of(AuthenticatorConditionOption.ALWAYS, AuthenticatorConditionOption.ON_IP_CHANGE, AuthenticatorConditionOption.UNKNOWN_IP, AuthenticatorConditionOption.UNKNOWN_LOCATION, AuthenticatorConditionOption.NEVER);
    private static final List<AuthenticatorActionOption> ACTION_OPTIONS = List.of(AuthenticatorActionOption.SEND_IP_WARNING_EMAIL, AuthenticatorActionOption.DENY_ACCESS, AuthenticatorActionOption.LOG, AuthenticatorActionOption.DISABLE_USER);

    private static final ProviderConfigProperty CONDITION_PROPERTY = AuthenticatorConditionConfigPropertyBuilder.getChooseConditionConfigProperty(CONDITION_OPTIONS);
    private static final ProviderConfigProperty ACTION_PROPERTY = AuthenticatorActionConfigPropertyBuilder.getChooseActionConfigProperty(ACTION_OPTIONS);

    static final List<ProviderConfigProperty> CONFIG_PROPERTIES = ProviderConfigurationBuilder.create()
            .property(CONDITION_PROPERTY)
            .property(ACTION_PROPERTY)
            .build();

    private IpAuthenticatorConfigProperties() {
    }
}
