package org.b2code.authentication;

import org.b2code.authentication.ip.UnknownIpConditionalAuthenticatorFactory;
import org.b2code.config.MaxmindGeoLiteFileServerConfig;
import org.junit.jupiter.api.Test;
import org.keycloak.testframework.annotations.KeycloakIntegrationTest;

@KeycloakIntegrationTest(config = MaxmindGeoLiteFileServerConfig.class)
class UnknownIpConditionalAuthenticatorProviderTest extends BaseConditionalAuthenticatorProviderTest {

    @Override
    String getConditionalProviderToTest() {
        return UnknownIpConditionalAuthenticatorFactory.PROVIDER_ID;
    }

    @Test
    void testDeniesOnFirstLogin() {
        loginAndExpectFail();
    }
}
