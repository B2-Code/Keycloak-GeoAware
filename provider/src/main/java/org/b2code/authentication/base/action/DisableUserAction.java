package org.b2code.authentication.base.action;

import lombok.NoArgsConstructor;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.models.KeycloakSession;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class DisableUserAction implements AuthenticatorAction {

    public static final String LABEL = "Disable user";

    private static final DisableUserAction INSTANCE = new DisableUserAction();

    @Override
    public void execute(KeycloakSession session, AuthenticationFlowContext context) {
        context.getUser().setEnabled(false);
    }

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    public String getHelpText() {
        return "Disables the user so that subsequent login attempts are rejected until the user is enabled again.";
    }

    public static DisableUserAction instance() {
        return INSTANCE;
    }
}
