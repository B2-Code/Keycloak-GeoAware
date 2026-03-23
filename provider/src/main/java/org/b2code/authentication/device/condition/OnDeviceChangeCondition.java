package org.b2code.authentication.device.condition;

import lombok.NoArgsConstructor;
import org.b2code.authentication.base.condition.AuthenticatorCondition;
import org.b2code.geoip.persistence.entity.Device;
import org.b2code.loginhistory.LoginHistoryProvider;
import org.keycloak.device.DeviceRepresentationProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.representations.account.DeviceRepresentation;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class OnDeviceChangeCondition implements AuthenticatorCondition {

    public static final String LABEL = "On device change";

    private static final OnDeviceChangeCondition INSTANCE = new OnDeviceChangeCondition();

    @Override
    public boolean check(KeycloakSession session) {
        DeviceRepresentationProvider deviceRepresentationProvider = session.getProvider(DeviceRepresentationProvider.class);
        LoginHistoryProvider loginHistoryProvider = session.getProvider(LoginHistoryProvider.class);
        DeviceRepresentation deviceRepresentation = deviceRepresentationProvider.deviceRepresentation();
        Device device = Device.fromDeviceRepresentation(deviceRepresentation);
        return loginHistoryProvider.getLastLogin()
                .map(l -> !l.getDevice().equals(device))
                .orElse(true);
    }

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    public String getHelpText() {
        return "Triggers if the user agent has changed compared to the last login.";
    }

    public static OnDeviceChangeCondition instance() {
        return INSTANCE;
    }
}
