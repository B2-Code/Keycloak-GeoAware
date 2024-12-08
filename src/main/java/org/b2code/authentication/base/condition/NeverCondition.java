package org.b2code.authentication.base.condition;

import lombok.NoArgsConstructor;
import org.keycloak.models.KeycloakSession;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class NeverCondition implements AuthenticatorCondition {

    public static final String LABEL = "Never";

    private static final NeverCondition INSTANCE = new NeverCondition();

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

    public static NeverCondition instance() {
        return INSTANCE;
    }
}
