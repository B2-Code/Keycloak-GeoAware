package org.b2code.authentication.device.condition;

import lombok.NoArgsConstructor;
import org.b2code.authentication.base.condition.AuthenticatorCondition;
import org.b2code.service.loginhistory.LoginHistoryProvider;
import org.keycloak.models.KeycloakSession;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class StrictOnUnknownDeviceCondition implements AuthenticatorCondition {

    public static final String LABEL = "Unknown Device";

    private static final StrictOnUnknownDeviceCondition INSTANCE = new StrictOnUnknownDeviceCondition();

    @Override
    public boolean check(KeycloakSession session) {
        LoginHistoryProvider loginHistoryProvider = session.getProvider(LoginHistoryProvider.class);
        return loginHistoryProvider.isKnownDevice();
    }

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    public String getHelpText() {
        return "Triggers when the device is unknown.";
    }

    public static StrictOnUnknownDeviceCondition instance() {
        return INSTANCE;
    }

}
