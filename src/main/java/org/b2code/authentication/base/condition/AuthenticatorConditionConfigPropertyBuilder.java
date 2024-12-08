package org.b2code.authentication.base.condition;

import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

public final class AuthenticatorConditionConfigPropertyBuilder {
    public static ProviderConfigProperty getChooseConditionConfigProperty(List<AuthenticatorConditionOption> conditionOptions) {
        ProviderConfigProperty property = new ProviderConfigProperty();
        property.setName(AuthenticatorCondition.CONFIG_PROPERTY_NAME);
        property.setLabel("Condition");
        property.setType(ProviderConfigProperty.LIST_TYPE);
        property.setRequired(true);
        property.setDefaultValue(NeverCondition.LABEL);
        StringBuilder helpText = new StringBuilder();
        helpText.append("Select the condition that should trigger the action.\n");
        List<String> options = conditionOptions.stream()
                .map(AuthenticatorConditionOption::getCondition)
                .map(AuthenticatorCondition::getLabel)
                .toList();
        for (AuthenticatorConditionOption option : conditionOptions) {
            AuthenticatorCondition condition = option.getCondition();
            helpText.append(condition.getLabel()).append(": ").append(condition.getHelpText()).append("\n");
        }
        property.setHelpText(helpText.toString());
        property.setOptions(options);
        return property;
    }
}
