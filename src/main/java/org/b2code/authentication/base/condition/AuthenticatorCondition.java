package org.b2code.authentication.base.condition;

import org.keycloak.models.KeycloakSession;

public interface AuthenticatorCondition {
    boolean check(KeycloakSession session);
    String getLabel();
    String getHelpText();
}
