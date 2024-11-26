package org.b2code.authentication.unknownip.condition;

import org.b2code.authentication.base.condition.AuthenticatorCondition;
import org.b2code.service.loginhistory.LoginHistoryProvider;
import org.keycloak.models.KeycloakSession;

public class UnknownIpCondition implements AuthenticatorCondition {

    public static final String LABEL = "Unknown IP";

    @Override
    public boolean check(KeycloakSession session) {
        LoginHistoryProvider loginHistoryProvider = session.getProvider(LoginHistoryProvider.class);
        return !loginHistoryProvider.isKnownIp();
    }

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    public String getHelpText() {
        return "Triggers when the IP address is unknown.";
    }
}
