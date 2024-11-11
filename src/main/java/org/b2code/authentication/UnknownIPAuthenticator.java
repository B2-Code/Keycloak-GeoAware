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

        UnknownIPAuthenticatorNotificationMode emailMode = getEmailMode(context);
        log.debugf("Email mode: %s", emailMode);

        context.success();
        sendNotification(emailMode);
    }

    private UnknownIPAuthenticatorNotificationMode getEmailMode(AuthenticationFlowContext context) {
        UnknownIPAuthenticatorConfig config = new UnknownIPAuthenticatorConfig(context.getAuthenticatorConfig());
        return config.getEmailMode();
    }

    private void sendNotification(UnknownIPAuthenticatorNotificationMode emailMode) {
        LoginHistoryProvider loginHistoryProvider = session.getProvider(LoginHistoryProvider.class);
        UserModel user = session.getContext().getAuthenticationSession().getAuthenticatedUser();
        String ip = session.getContext().getConnection().getRemoteAddr();

        if (emailMode == UnknownIPAuthenticatorNotificationMode.UNKNOWN_IP && !loginHistoryProvider.isKnownIp()) {
            log.debugf("New IP for user %s (%s), sending notification mail", user.getUsername(), ip);
            sentEmailNotification(user, ip);
        } else if (emailMode == UnknownIPAuthenticatorNotificationMode.ON_CHANGE && loginHistoryProvider.getLastLogin().map(l -> !l.getIp().equals(ip)).orElse(true)) {
            log.debugf("IP for user %s (%s) changed, sending notification mail", user.getUsername(), ip);
            sentEmailNotification(user, ip);
        } else if (emailMode == UnknownIPAuthenticatorNotificationMode.UNKNOWN_LOCATION && !loginHistoryProvider.isKnownLocation()) {
            log.debugf("New location for user %s (%s), sending notification mail", user.getUsername(), ip);
            sentEmailNotification(user, ip);
        } else if (emailMode == UnknownIPAuthenticatorNotificationMode.ALWAYS) {
            log.debugf("Sending always notification mail for user %s (%s)", user.getUsername(), ip);
            sentEmailNotification(user, ip);
        } else {
            log.debugf("The IP for user %s (%s) does not match the specified criteria", user.getUsername(), ip);
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
