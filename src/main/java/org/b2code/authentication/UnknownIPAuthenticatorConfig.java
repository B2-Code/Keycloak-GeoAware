package org.b2code.authentication;

import org.keycloak.models.AuthenticatorConfigModel;

public class UnknownIPAuthenticatorConfig {

    public static final String EMAIL_MODUS = "email-modus";

    private final AuthenticatorConfigModel authenticatorConfigModel;

    public UnknownIPAuthenticatorConfig(AuthenticatorConfigModel authenticatorConfigModel) {
        this.authenticatorConfigModel = authenticatorConfigModel;
    }


}
