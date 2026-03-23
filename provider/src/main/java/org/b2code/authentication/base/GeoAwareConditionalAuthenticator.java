package org.b2code.authentication.base;

import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.authentication.base.condition.AuthenticatorCondition;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.authenticators.conditional.ConditionalAuthenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

@JBossLog
@RequiredArgsConstructor
public class GeoAwareConditionalAuthenticator implements ConditionalAuthenticator {

    private final AuthenticatorCondition condition;

    @Override
    public boolean matchCondition(AuthenticationFlowContext context) {
        if (requiresUser() && context.getUser() == null) {
            log.warnf("User is required for condition '%s' but no user found in context", condition.getLabel());
        }
        return condition.check(context.getSession());
    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public void action(AuthenticationFlowContext context) {
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
    }

    @Override
    public void close() {
    }
}
