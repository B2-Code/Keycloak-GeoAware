package org.b2code.authentication.base.action;

import jakarta.ws.rs.core.Response;
import org.b2code.PluginConstants;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.events.Errors;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.messages.Messages;

public class DenyAccessAction implements AuthenticatorAction {

    public static final String LABEL = "Deny Access";

    @Override
    public void execute(KeycloakSession session, AuthenticationFlowContext context) {
        context.getEvent()
                .user(context.getUser())
                .detail("reason", "Access denied by " + PluginConstants.PLUGIN_NAME)
                .error(Errors.ACCESS_DENIED);
        Response challenge = context.form()
                .setError(Messages.ACCESS_DENIED)
                .createErrorPage(Response.Status.UNAUTHORIZED);
        context.failure(AuthenticationFlowError.ACCESS_DENIED, challenge);
    }

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    public String getHelpText() {
        return "Aborts the authentication process and denies access to the user.";
    }

}
