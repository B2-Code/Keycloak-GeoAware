package org.b2code.authentication.unknownip.condition;

import lombok.NoArgsConstructor;
import org.b2code.authentication.base.condition.AuthenticatorCondition;
import org.b2code.loginhistory.LoginHistoryProvider;
import org.keycloak.models.KeycloakSession;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class UnknownLocationCondition implements AuthenticatorCondition {

    public static final String LABEL = "Unknown Location";

    private static final UnknownLocationCondition INSTANCE = new UnknownLocationCondition();

    @Override
    public boolean check(KeycloakSession session) {
        LoginHistoryProvider loginHistoryProvider = session.getProvider(LoginHistoryProvider.class);
        return !loginHistoryProvider.isKnownLocation();
    }

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    public String getHelpText() {
        return "Triggers when the location is unknown.";
    }

    public static UnknownLocationCondition instance() {
        return INSTANCE;
    }
}
