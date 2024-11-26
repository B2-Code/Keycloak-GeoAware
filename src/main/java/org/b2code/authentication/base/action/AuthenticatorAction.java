package org.b2code.authentication.base.action;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.models.KeycloakSession;

public interface AuthenticatorAction {

    void execute(KeycloakSession session, AuthenticationFlowContext context);
    String getLabel();
    String getHelpText();
}
