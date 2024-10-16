package org.b2code.authentication;

import org.keycloak.models.AuthenticatorConfigModel;

public class UnknownIPAuthenticatorConfig {

    public static final String MODUS = "modus";

    private final AuthenticatorConfigModel authenticatorConfigModel;

    public UnknownIPAuthenticatorConfig(AuthenticatorConfigModel authenticatorConfigModel) {
        this.authenticatorConfigModel = authenticatorConfigModel;
    }


}
