package org.b2code.authentication;

import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.geoip.GeoIpInfo;
import org.b2code.geoip.database.GeoipDatabaseAccessProvider;
import org.b2code.service.loginhistory.LoginHistoryProvider;
import org.b2code.service.mail.EmailHelper;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.device.DeviceRepresentationProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.representations.account.DeviceRepresentation;

@JBossLog
@RequiredArgsConstructor
public class UnknownIPAuthenticator implements Authenticator {

    private final KeycloakSession session;

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        String ipAddress = context.getConnection().getRemoteAddr();
        log.debugf("IP address: %s", ipAddress);

        NotificationMode emailMode = getEmailMode(context);
        log.debugf("Email mode: %s", emailMode);

        context.success();
        trackIp(emailMode);
    }

    private NotificationMode getEmailMode(AuthenticationFlowContext context) {
        UnknownIPAuthenticatorConfig config = new UnknownIPAuthenticatorConfig(context.getAuthenticatorConfig());
        return config.getEmailModus();
    }

    private void trackIp(NotificationMode emailMode) {
        LoginHistoryProvider loginHistoryProvider = session.getProvider(LoginHistoryProvider.class);
        UserModel user = session.getContext().getAuthenticationSession().getAuthenticatedUser();
        String ip = session.getContext().getConnection().getRemoteAddr();

        if (!loginHistoryProvider.isKnownIp() && emailMode.equals(NotificationMode.UNKNOWN_IP)) {
            log.debugf("New IP for user %s (%s), sending notification mail", user.getUsername(), ip);
            sentEmailNotification(user, ip);
        } else if (emailMode.equals(NotificationMode.ALWAYS)) {
            log.debugf("Sending always notification mail for user %s (%s)", user.getUsername(), ip);
            sentEmailNotification(user, ip);
        }
    }

    /**
     * Sends an email notification to the user about login and the IP address.
     *
     * @param user the user
     * @param ip   the new IP address
     */
    private void sentEmailNotification(UserModel user, String ip) {
        GeoipDatabaseAccessProvider geoipDatabaseAccessProvider = session.getProvider(GeoipDatabaseAccessProvider.class);
        DeviceRepresentationProvider userAgentParserProvider = session.getProvider(DeviceRepresentationProvider.class);
        GeoIpInfo geoIpInfo = geoipDatabaseAccessProvider.getIpInfo(ip);
        DeviceRepresentation userAgentInfo = userAgentParserProvider.deviceRepresentation();
        EmailHelper.sendNewIpEmail(geoIpInfo, userAgentInfo, session, user, session.getContext().getRealm());
    }

    @Override
    public void action(AuthenticationFlowContext context) {
    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        // NOOP
    }

    @Override
    public void close() {
        // NOOP
    }
}
