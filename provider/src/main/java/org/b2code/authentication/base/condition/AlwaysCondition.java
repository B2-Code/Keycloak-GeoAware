package org.b2code.authentication.base.condition;

import lombok.NoArgsConstructor;
import org.keycloak.models.KeycloakSession;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class AlwaysCondition implements AuthenticatorCondition {

    public static final String LABEL = "Always";

    private static final AlwaysCondition INSTANCE = new AlwaysCondition();

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

    public static AlwaysCondition instance() {
        return INSTANCE;
    }
}
