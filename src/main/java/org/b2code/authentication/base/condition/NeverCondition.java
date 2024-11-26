package org.b2code.authentication.base.condition;

import org.keycloak.models.KeycloakSession;

public class NeverCondition implements AuthenticatorCondition {

    public static final String LABEL = "Never";

    @Override
    public boolean check(KeycloakSession session) {
        return false;
    }

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    public String getHelpText() {
        return "Triggers never.";
    }
}
