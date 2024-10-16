package org.b2code.authentication;

import org.keycloak.authentication.AuthenticationFlowContext;

public class UnknownIPAuthenticationFlowContext {

    private final AuthenticationFlowContext context;
    private UnknownIPAuthenticatorConfig config;

    UnknownIPAuthenticationFlowContext(AuthenticationFlowContext context) {
        this.context = context;
    }

    UnknownIPAuthenticatorConfig config() {
        if (config == null) {
            config = new UnknownIPAuthenticatorConfig(context.getAuthenticatorConfig());
        }
        return config;
    }
}
