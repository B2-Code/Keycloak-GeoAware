package org.b2code.authentication.unknownip.condition;

import org.b2code.authentication.base.condition.AuthenticatorCondition;
import org.b2code.service.loginhistory.LoginHistoryProvider;
import org.keycloak.models.KeycloakSession;

public class UnknownLocationCondition implements AuthenticatorCondition {

        public static final String LABEL = "Unknown Location";

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
}
