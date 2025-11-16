package org.b2code.authentication.unknownip.action;

import lombok.NoArgsConstructor;
import org.b2code.authentication.base.action.AuthenticatorAction;
import org.b2code.geoip.persistence.entity.GeoIpInfo;
import org.b2code.geoip.provider.GeoIpProvider;
import org.b2code.mail.EmailHelper;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.device.DeviceRepresentationProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.keycloak.representations.account.DeviceRepresentation;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class SendIpWarningEmailAction implements AuthenticatorAction {

    public static final String LABEL = "Notification Email (IP)";

    private static final SendIpWarningEmailAction INSTANCE = new SendIpWarningEmailAction();

    @Override
    public void execute(KeycloakSession session, AuthenticationFlowContext context) {
        GeoIpProvider geoipProvider = session.getProvider(GeoIpProvider.class);
        DeviceRepresentationProvider userAgentParserProvider = session.getProvider(DeviceRepresentationProvider.class);
        String ip = session.getContext().getConnection().getRemoteAddr();
        UserModel user = session.getContext().getAuthenticationSession().getAuthenticatedUser();
        GeoIpInfo geoIpInfo = geoipProvider.getIpInfo(ip);
        DeviceRepresentation userAgentInfo = userAgentParserProvider.deviceRepresentation();
        EmailHelper.sendNewIpEmail(geoIpInfo, userAgentInfo, session, user, session.getContext().getRealm());
    }

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    public String getHelpText() {
        return "Sends an email to the user with the new IP address and location.";
    }

    public static SendIpWarningEmailAction instance() {
        return INSTANCE;
    }
}
