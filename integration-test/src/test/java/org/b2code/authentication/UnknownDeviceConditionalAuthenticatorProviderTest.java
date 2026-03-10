package org.b2code.authentication;

import org.b2code.authentication.device.UnknownDeviceConditionalAuthenticatorFactory;
import org.b2code.config.MaxmindGeoLiteFileServerConfig;
import org.junit.jupiter.api.Test;
import org.keycloak.testframework.annotations.KeycloakIntegrationTest;

@KeycloakIntegrationTest(config = MaxmindGeoLiteFileServerConfig.class)
class UnknownDeviceConditionalAuthenticatorProviderTest extends BaseConditionalAuthenticatorProviderTest {

    @Override
    String getConditionalProviderToTest() {
        return UnknownDeviceConditionalAuthenticatorFactory.PROVIDER_ID;
    }

    @Test
    void testDeniesOnFirstLogin() {
        loginAndExpectDenied();
    }
}
