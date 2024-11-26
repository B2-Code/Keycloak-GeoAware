package org.b2code.authentication.unknownip.action;

import org.b2code.authentication.base.action.AuthenticatorAction;
import org.b2code.geoip.GeoIpInfo;
import org.b2code.geoip.database.GeoipDatabaseAccessProvider;
import org.b2code.service.mail.EmailHelper;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.device.DeviceRepresentationProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.keycloak.representations.account.DeviceRepresentation;

public class SendIpWarningEmailAction implements AuthenticatorAction {

    public static final String LABEL = "Notification (Email)";

    @Override
    public void execute(KeycloakSession session, AuthenticationFlowContext context) {
        GeoipDatabaseAccessProvider geoipDatabaseAccessProvider = session.getProvider(GeoipDatabaseAccessProvider.class);
        DeviceRepresentationProvider userAgentParserProvider = session.getProvider(DeviceRepresentationProvider.class);
        String ip = session.getContext().getConnection().getRemoteAddr();
        UserModel user = session.getContext().getAuthenticationSession().getAuthenticatedUser();
        GeoIpInfo geoIpInfo = geoipDatabaseAccessProvider.getIpInfo(ip);
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

}
