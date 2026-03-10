package org.b2code.authentication;

import org.b2code.authentication.ip.OnIpChangeConditionalAuthenticatorFactory;
import org.b2code.config.MaxmindGeoLiteFileServerConfig;
import org.junit.jupiter.api.Test;
import org.keycloak.testframework.annotations.KeycloakIntegrationTest;

@KeycloakIntegrationTest(config = MaxmindGeoLiteFileServerConfig.class)
class OnIpChangeConditionalAuthenticatorProviderTest extends BaseConditionalAuthenticatorProviderTest {

    @Override
    String getConditionalProviderToTest() {
        return OnIpChangeConditionalAuthenticatorFactory.PROVIDER_ID;
    }

    @Test
    void testDeniesOnFirstLogin() {
        loginAndExpectDenied();
    }
}
