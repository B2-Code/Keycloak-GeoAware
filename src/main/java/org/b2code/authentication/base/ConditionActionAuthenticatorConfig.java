package org.b2code.authentication.base;

import org.b2code.authentication.base.action.ActionFactory;
import org.b2code.authentication.base.action.AuthenticatorAction;
import org.b2code.authentication.base.condition.AuthenticatorCondition;
import org.b2code.authentication.base.condition.ConditionFactory;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.Constants;

import java.util.List;
import java.util.stream.Stream;

public class ConditionActionAuthenticatorConfig {

    private final AuthenticatorConfigModel authenticatorConfigModel;

    public ConditionActionAuthenticatorConfig(AuthenticatorConfigModel authenticatorConfigModel) {
        this.authenticatorConfigModel = authenticatorConfigModel;
    }

    public AuthenticatorCondition getCondition() {
        String label = authenticatorConfigModel.getConfig().get(ConditionFactory.CONDITION_PROPERTY_NAME);
        return ConditionFactory.getConditionByLabel(label);
    }

    public List<AuthenticatorAction> getActions() {
        String labels = authenticatorConfigModel.getConfig().get(ActionFactory.ACTION_PROPERTY_NAME);
        return Stream.of(labels.split(Constants.CFG_DELIMITER)).map(ActionFactory::getActionByLabel).toList();
    }
}
