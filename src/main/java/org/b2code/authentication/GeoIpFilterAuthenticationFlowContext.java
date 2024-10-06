package org.b2code.authentication;

import org.keycloak.authentication.AuthenticationFlowContext;

public class GeoIpFilterAuthenticationFlowContext {

    private final AuthenticationFlowContext context;
    private GeoIpFilterAuthenticatorConfig config;

    GeoIpFilterAuthenticationFlowContext(AuthenticationFlowContext context) {
        this.context = context;
    }

    GeoIpFilterAuthenticatorConfig config() {
        if (config == null) {
            config = new GeoIpFilterAuthenticatorConfig(context.getAuthenticatorConfig());
        }
        return config;
    }

}
