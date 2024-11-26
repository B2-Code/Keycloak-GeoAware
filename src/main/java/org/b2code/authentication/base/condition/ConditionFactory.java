package org.b2code.authentication.base.condition;

import org.b2code.authentication.unknownip.condition.OnIpChangeCondition;
import org.b2code.authentication.unknownip.condition.UnknownIpCondition;
import org.b2code.authentication.unknownip.condition.UnknownLocationCondition;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConditionFactory {

    public static final String CONDITION_PROPERTY_NAME = "condition";

    private static final Map<String, AuthenticatorCondition> conditionMap = new HashMap<>();

    static {
        conditionMap.put(AlwaysCondition.LABEL, new AlwaysCondition());
        conditionMap.put(NeverCondition.LABEL, new NeverCondition());
        conditionMap.put(OnIpChangeCondition.LABEL, new OnIpChangeCondition());
        conditionMap.put(UnknownLocationCondition.LABEL, new UnknownLocationCondition());
        conditionMap.put(UnknownIpCondition.LABEL, new UnknownIpCondition());
    }

    public static AuthenticatorCondition getConditionByLabel(String label) {
        return conditionMap.get(label);
    }

    public static ProviderConfigProperty getChooseConditionConfigProperty(List<String> labels) {
        ProviderConfigProperty property = new ProviderConfigProperty();
        property.setName(CONDITION_PROPERTY_NAME);
        property.setLabel("Condition");
        property.setType(ProviderConfigProperty.LIST_TYPE);
        property.setRequired(true);
        property.setDefaultValue(NeverCondition.LABEL);
        StringBuilder helpText = new StringBuilder();
        helpText.append("Select the condition that should trigger the action.\n");
        List<String> options = new ArrayList<>();
        for (String label : labels) {
            AuthenticatorCondition condition = getConditionByLabel(label);
            options.add(condition.getLabel());
            helpText.append(condition.getLabel()).append(": ").append(condition.getHelpText()).append("\n");
        }
        property.setHelpText(helpText.toString());
        property.setOptions(options);
        return property;
    }
}