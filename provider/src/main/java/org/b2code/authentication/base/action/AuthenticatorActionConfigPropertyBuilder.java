package org.b2code.authentication.base.action;

import lombok.NoArgsConstructor;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class AuthenticatorActionConfigPropertyBuilder {

    public static ProviderConfigProperty getChooseActionConfigProperty(List<AuthenticatorActionOption> actionOptions) {
        ProviderConfigProperty property = new ProviderConfigProperty();
        property.setType(ProviderConfigProperty.MULTIVALUED_LIST_TYPE);
        property.setName(AuthenticatorAction.CONFIG_PROPERTY_NAME);
        property.setLabel("Actions");
        property.setHelpText("Action to execute");
        property.setRequired(true);
        property.setDefaultValue(LogAction.LABEL);
        List<String> options = actionOptions.stream().map(AuthenticatorActionOption::getAction).map(AuthenticatorAction::getLabel).toList();
        StringBuilder helpText = new StringBuilder();
        helpText.append("Select the action that should be executed.\n");
        for (AuthenticatorActionOption option : actionOptions) {
            AuthenticatorAction action = option.getAction();
            helpText.append(action.getLabel()).append(": ").append(action.getHelpText()).append("\n");
        }
        property.setOptions(options);
        property.setHelpText(helpText.toString());
        return property;
    }

}
