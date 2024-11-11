package org.b2code.authentication;

import org.keycloak.models.AuthenticatorConfigModel;

public class UnknownIPAuthenticatorConfig {

    public static final String EMAIL_MODE = "email-mode";

    private final AuthenticatorConfigModel authenticatorConfigModel;

    public UnknownIPAuthenticatorConfig(AuthenticatorConfigModel authenticatorConfigModel) {
        this.authenticatorConfigModel = authenticatorConfigModel;
    }

    public UnknownIPAuthenticatorNotificationMode getEmailMode() {
        // Email mode is required, so it should always be present
        return UnknownIPAuthenticatorNotificationMode.getByLabel(authenticatorConfigModel.getConfig().get(EMAIL_MODE)).orElseThrow(() -> new IllegalArgumentException("Email notification mode not found"));
    }

}
