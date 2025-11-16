package org.b2code.geoip;

import org.b2code.config.MaxmindFileAutodownloadServerConfig;
import org.junit.jupiter.api.Disabled;
import org.keycloak.testframework.annotations.KeycloakIntegrationTest;

@Disabled
@KeycloakIntegrationTest(config = MaxmindFileAutodownloadServerConfig.class)
class MaxmindFileAutodownloadProviderTest extends BaseGeoIpProviderTest {

}
