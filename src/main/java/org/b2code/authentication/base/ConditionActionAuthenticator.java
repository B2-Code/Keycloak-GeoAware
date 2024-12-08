package org.b2code.authentication.base;

import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.b2code.PluginConstants;
import org.b2code.admin.PluginConfigWrapper;
import org.b2code.authentication.base.action.AuthenticatorAction;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.util.List;

@JBossLog
@RequiredArgsConstructor
public class ConditionActionAuthenticator implements Authenticator {

    private final KeycloakSession session;

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        PluginConfigWrapper pluginConfigWrapper = new PluginConfigWrapper(context.getRealm());
        if (!pluginConfigWrapper.isPluginEnabled()) {
            log.warnf("%s is disabled, but it is used in the '%s' flow", PluginConstants.PLUGIN_NAME, context.getTopLevelFlow().getAlias());
            context.success();
            return;
        }

        ConditionActionAuthenticatorConfig config = new ConditionActionAuthenticatorConfig(context.getAuthenticatorConfig());

        if (config.getCondition().check(session)) {
            List<AuthenticatorAction> actions = config.getActions();
            for (AuthenticatorAction action : actions) {
                log.debugf("Executing action: %s", action.getLabel());
                action.execute(session, context);
            }
        }

        if (context.getStatus() == null) {
            context.success();
        }
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
