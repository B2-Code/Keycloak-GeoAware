package org.b2code.authentication.unknownip.condition;

import lombok.NoArgsConstructor;
import org.b2code.authentication.base.condition.AuthenticatorCondition;
import org.b2code.service.loginhistory.LoginHistoryProvider;
import org.keycloak.models.KeycloakSession;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class OnIpChangeCondition implements AuthenticatorCondition {

    public static final String LABEL = "On IP Change";

    private static final OnIpChangeCondition INSTANCE = new OnIpChangeCondition();

    @Override
    public boolean check(KeycloakSession session) {
        LoginHistoryProvider loginHistoryProvider = session.getProvider(LoginHistoryProvider.class);
        return loginHistoryProvider.getLastLogin().map(l -> !l.getIp().equals(session.getContext().getConnection().getRemoteAddr())).orElse(true);
    }

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    public String getHelpText() {
        return "Triggers when the IP address changes.";
    }

    public static OnIpChangeCondition instance() {
        return INSTANCE;
    }
}
