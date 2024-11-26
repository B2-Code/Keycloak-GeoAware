package org.b2code.authentication.unknownip;

import org.b2code.authentication.base.action.DisableUserAction;
import org.b2code.authentication.unknownip.action.SendIpWarningEmailAction;
import org.b2code.authentication.unknownip.condition.OnIpChangeCondition;
import org.b2code.authentication.unknownip.condition.UnknownIpCondition;
import org.b2code.authentication.unknownip.condition.UnknownLocationCondition;
import org.b2code.authentication.base.action.ActionFactory;
import org.b2code.authentication.base.action.DenyAccessAction;
import org.b2code.authentication.base.action.LogAction;
import org.b2code.authentication.base.condition.AlwaysCondition;
import org.b2code.authentication.base.condition.ConditionFactory;
import org.b2code.authentication.base.condition.NeverCondition;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;

import java.util.List;

public class UnknownIPAuthenticatorConfigProperties {

    private static final List<String> CONDITION_OPTIONS = List.of(AlwaysCondition.LABEL, OnIpChangeCondition.LABEL, UnknownIpCondition.LABEL, UnknownLocationCondition.LABEL, NeverCondition.LABEL);
    private static final List<String> ACTION_OPTIONS = List.of(SendIpWarningEmailAction.LABEL, DenyAccessAction.LABEL, LogAction.LABEL, DisableUserAction.LABEL);

    private static final ProviderConfigProperty CONDITION_PROPERTY = ConditionFactory.getChooseConditionConfigProperty(CONDITION_OPTIONS);
    private static final ProviderConfigProperty ACTION_PROPERTY = ActionFactory.getChooseActionConfigProperty(ACTION_OPTIONS);

    protected static final List<ProviderConfigProperty> CONFIG_PROPERTIES = ProviderConfigurationBuilder.create()
            .property(CONDITION_PROPERTY)
            .property(ACTION_PROPERTY)
            .build();

    private UnknownIPAuthenticatorConfigProperties() {
    }
}
