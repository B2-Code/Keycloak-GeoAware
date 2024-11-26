package org.b2code.authentication.base.action;

import org.b2code.authentication.unknownip.action.SendIpWarningEmailAction;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionFactory {

    public static final String ACTION_PROPERTY_NAME = "action";

    private static final Map<String, AuthenticatorAction> actionMap = new HashMap<>();

    static {
        actionMap.put(SendIpWarningEmailAction.LABEL, new SendIpWarningEmailAction());
        actionMap.put(DenyAccessAction.LABEL, new DenyAccessAction());
        actionMap.put(LogAction.LABEL, new LogAction());
        actionMap.put(DisableUserAction.LABEL, new DisableUserAction());
    }

    public static AuthenticatorAction getActionByLabel(String label) {
        return actionMap.get(label);
    }


    public static ProviderConfigProperty getChooseActionConfigProperty(List<String> labels) {
        ProviderConfigProperty property = new ProviderConfigProperty();
        property.setType(ProviderConfigProperty.MULTIVALUED_LIST_TYPE);
        property.setName(ACTION_PROPERTY_NAME);
        property.setLabel("Actions");
        property.setHelpText("Action to execute");
        property.setRequired(true);
        property.setDefaultValue(LogAction.LABEL);
        List<String> options = new ArrayList<>();
        StringBuilder helpText = new StringBuilder();
        helpText.append("Select the action that should be executed.\n");
        for (String label : labels) {
            AuthenticatorAction action = getActionByLabel(label);
            options.add(action.getLabel());
            helpText.append(action.getLabel()).append(": ").append(action.getHelpText()).append("\n");
        }
        property.setOptions(options);
        property.setHelpText(helpText.toString());
        return property;
    }
}
