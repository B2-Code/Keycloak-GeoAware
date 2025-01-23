package org.b2code.authentication.device.action;

import lombok.NoArgsConstructor;
import org.b2code.authentication.base.action.AuthenticatorAction;
import org.b2code.geoip.GeoIpInfo;
import org.b2code.geoip.GeoIpProvider;
import org.b2code.geoip.GeoIpProviderFactory;
import org.b2code.service.mail.EmailHelper;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.device.DeviceRepresentationProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.keycloak.representations.account.DeviceRepresentation;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class SendDeviceWarningEmailAction implements AuthenticatorAction {

    public static final String LABEL = "Notification Email (Device)";

    private static final SendDeviceWarningEmailAction INSTANCE = new SendDeviceWarningEmailAction();

    @Override
    public void execute(KeycloakSession session, AuthenticationFlowContext context) {
        GeoIpProvider geoipProvider = GeoIpProviderFactory.getProvider(session);
        DeviceRepresentationProvider userAgentParserProvider = session.getProvider(DeviceRepresentationProvider.class);
        String ip = session.getContext().getConnection().getRemoteAddr();
        UserModel user = session.getContext().getAuthenticationSession().getAuthenticatedUser();
        GeoIpInfo geoIpInfo = geoipProvider.getIpInfo(ip);
        DeviceRepresentation userAgentInfo = userAgentParserProvider.deviceRepresentation();
        EmailHelper.sendNewDeviceEmail(geoIpInfo, userAgentInfo, session, user, session.getContext().getRealm());
    }

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    public String getHelpText() {
        return "Sends an email to the user with the new device information.";
    }

    public static SendDeviceWarningEmailAction instance() {
        return INSTANCE;
    }

}
