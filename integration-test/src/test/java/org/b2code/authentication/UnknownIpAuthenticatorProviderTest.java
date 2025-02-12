package org.b2code.authentication;

import org.b2code.config.MaxmindGeoLiteFileServerConfig;
import org.keycloak.representations.idm.AuthenticationExecutionRepresentation;
import org.keycloak.testframework.annotations.KeycloakIntegrationTest;

@KeycloakIntegrationTest(config = MaxmindGeoLiteFileServerConfig.class)
class UnknownIpAuthenticatorProviderTest extends BaseAuthenticatorProviderTest {

    private static final String PROVIDER_ID = "geoaware-unknown-ip";

    @Override
    AuthenticationExecutionRepresentation getAuthenticatorToTest() {
        AuthenticationExecutionRepresentation authenticatorToTest = new AuthenticationExecutionRepresentation();
        authenticatorToTest.setAuthenticator(PROVIDER_ID);
        authenticatorToTest.setRequirement("REQUIRED");
        return authenticatorToTest;
    }
}
