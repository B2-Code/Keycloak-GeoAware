package org.b2code.geoip;

import org.b2code.config.MaxmindFileAutodownloadServerConfig;
import org.keycloak.testframework.annotations.KeycloakIntegrationTest;

@KeycloakIntegrationTest(config = MaxmindFileAutodownloadServerConfig.class)
class MaxmindFileAutodownloadProviderTest extends BaseGeoIpProviderTest {

}
