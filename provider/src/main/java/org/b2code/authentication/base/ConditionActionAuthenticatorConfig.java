package org.b2code.authentication.base;

import lombok.RequiredArgsConstructor;
import org.b2code.authentication.base.action.AuthenticatorActionOption;
import org.b2code.authentication.base.action.AuthenticatorAction;
import org.b2code.authentication.base.condition.AuthenticatorCondition;
import org.b2code.authentication.base.condition.AuthenticatorConditionOption;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.Constants;

import java.util.List;

@RequiredArgsConstructor
public class ConditionActionAuthenticatorConfig {

    private final AuthenticatorConfigModel authenticatorConfigModel;

    public AuthenticatorCondition getCondition() {
        String label = authenticatorConfigModel.getConfig().get(AuthenticatorCondition.CONFIG_PROPERTY_NAME);
        return AuthenticatorConditionOption.getConditionByLabel(label);
    }

    public List<AuthenticatorAction> getActions() {
        String labels = authenticatorConfigModel.getConfig().get(AuthenticatorAction.CONFIG_PROPERTY_NAME);
        return Constants.CFG_DELIMITER_PATTERN.splitAsStream(labels).map(AuthenticatorActionOption::getActionByLabel).toList();
    }
}
