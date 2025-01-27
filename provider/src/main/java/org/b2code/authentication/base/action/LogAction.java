package org.b2code.authentication.base.action;

import lombok.NoArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.models.KeycloakSession;

import java.util.Objects;

@JBossLog
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class LogAction implements AuthenticatorAction {

    public static final String LABEL = "Log";

    private static final LogAction INSTANCE = new LogAction();

    @Override
    public void execute(KeycloakSession session, AuthenticationFlowContext context) {
        String authenticatorName = Objects.requireNonNullElse(context.getAuthenticatorConfig().getAlias(), context.getAuthenticatorConfig().getId());
        log.infof("Condition of authenticator '%s' has been met. User: %s (%s)", authenticatorName, context.getUser().getUsername(), context.getUser().getId());
    }

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    public String getHelpText() {
        return "Logs a message.";
    }

    public static LogAction instance() {
        return INSTANCE;
    }

}
