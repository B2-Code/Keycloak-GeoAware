package org.b2code.authentication.base.condition;

import org.keycloak.models.KeycloakSession;

public interface AuthenticatorCondition {

    String CONFIG_PROPERTY_NAME = "condition";

    boolean check(KeycloakSession session);

    String getLabel();

    String getHelpText();
}
