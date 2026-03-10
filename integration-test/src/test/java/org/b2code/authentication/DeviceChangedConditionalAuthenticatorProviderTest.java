package org.b2code.authentication;

import org.b2code.authentication.device.OnDeviceChangeConditionalAuthenticatorFactory;
import org.b2code.config.MaxmindGeoLiteFileServerConfig;
import org.junit.jupiter.api.Test;
import org.keycloak.testframework.annotations.KeycloakIntegrationTest;

@KeycloakIntegrationTest(config = MaxmindGeoLiteFileServerConfig.class)
class DeviceChangedConditionalAuthenticatorProviderTest extends BaseConditionalAuthenticatorProviderTest {

    @Override
    String getConditionalProviderToTest() {
        return OnDeviceChangeConditionalAuthenticatorFactory.PROVIDER_ID;
    }

    @Test
    void testDeniesOnFirstLogin() {
        loginAndExpectDenied();
    }
}
