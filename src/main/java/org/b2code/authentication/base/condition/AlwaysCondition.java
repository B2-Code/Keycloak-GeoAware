package org.b2code.authentication.base.condition;

import org.keycloak.models.KeycloakSession;

public class AlwaysCondition implements AuthenticatorCondition {

    public static final String LABEL = "Always";

    @Override
    public boolean check(KeycloakSession session) {
        return true;
    }

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    public String getHelpText() {
        return "Triggers every time.";
    }
}
