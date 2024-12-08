package org.b2code.authentication.base.action;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.models.KeycloakSession;

public interface AuthenticatorAction {

    String CONFIG_PROPERTY_NAME = "action";

    void execute(KeycloakSession session, AuthenticationFlowContext context);

    String getLabel();

    String getHelpText();

}
