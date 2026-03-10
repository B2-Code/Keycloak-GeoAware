package org.b2code.authentication;

import org.b2code.authentication.ip.UnknownLocationConditionalAuthenticatorFactory;
import org.b2code.config.MaxmindGeoLiteFileServerConfig;
import org.junit.jupiter.api.Test;
import org.keycloak.testframework.annotations.KeycloakIntegrationTest;

@KeycloakIntegrationTest(config = MaxmindGeoLiteFileServerConfig.class)
class UnknownLocationConditionalAuthenticatorProviderTest extends BaseConditionalAuthenticatorProviderTest {

    @Override
    String getConditionalProviderToTest() {
        return UnknownLocationConditionalAuthenticatorFactory.PROVIDER_ID;
    }

    @Test
    void testDeniesOnFirstLogin() {
        loginFromIpAndExpectDenied("2.125.160.217");
    }
}
