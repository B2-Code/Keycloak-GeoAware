package org.b2code.authentication;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.geoip.GeoIpInfo;
import org.b2code.geoip.database.GeoipDatabaseAccessProvider;
import org.b2code.service.loginhistory.LoginHistoryProvider;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.events.Details;
import org.keycloak.events.Errors;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.messages.Messages;

@JBossLog
@RequiredArgsConstructor
public class GeoIpFilterAuthenticator implements Authenticator {

    private final KeycloakSession session;

    @Override
    public void authenticate(AuthenticationFlowContext authenticationFlowContext) {
        GeoIpFilterAuthenticationFlowContext geoIpFilterContext = new GeoIpFilterAuthenticationFlowContext(authenticationFlowContext);
        String ipAddress = authenticationFlowContext.getConnection().getRemoteAddr();
        log.debugf("IP address: %s", ipAddress);

        if (true) {
            authenticationFlowContext.success();
            trackIp();
        } else {
            denyAccess(authenticationFlowContext);
        }
    }

    private void denyAccess(AuthenticationFlowContext authenticationFlowContext) {
        authenticationFlowContext.getEvent().detail(Details.REASON, GeoIpFilterAuthenticatorFactory.DISPLAY_TYPE).error(Errors.ACCESS_DENIED);
        Response response = authenticationFlowContext.form().setError(Messages.ACCESS_DENIED).createErrorPage(Response.Status.FORBIDDEN);
        authenticationFlowContext.failure(AuthenticationFlowError.ACCESS_DENIED, response);
    }

    private GeoIpInfo getIpInfo(AuthenticationFlowContext context) {
        return session.getProvider(GeoipDatabaseAccessProvider.class).getIpInfo(context.getConnection().getRemoteAddr());
    }

    private void trackIp() {
        session.getProvider(LoginHistoryProvider.class).track();
        session.getProvider(LoginHistoryProvider.class).track();
    }

    @Override
    public void action(AuthenticationFlowContext authenticationFlowContext) {

    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public boolean configuredFor(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
        // NOOP
    }

    @Override
    public void close() {
        // NOOP
    }
}
