package org.b2code.authentication;

import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.service.iphistory.IpHistoryProvider;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

@JBossLog
@RequiredArgsConstructor
public class UnknownIPAuthenticator implements Authenticator {

    private final KeycloakSession session;

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        String ipAddress = context.getConnection().getRemoteAddr();
        log.debugf("IP address: %s", ipAddress);

        NotificationMode emailModus = getEmailModus(context);
        log.debugf("Email modus: %s", emailModus);

        context.success();
        trackIp(ipAddress, context.getUser().getId(), emailModus);
    }

    private NotificationMode getEmailModus(AuthenticationFlowContext context) {
        UnknownIPAuthenticatorConfig config = new UnknownIPAuthenticatorConfig(context.getAuthenticatorConfig());
        return config.getEmailModus();
    }

    private void trackIp(String ip, String userId, NotificationMode emailModus) {
        session.getProvider(IpHistoryProvider.class).track(ip, userId, emailModus);
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
