package org.b2code.geoip;

import org.b2code.config.IpInfoServerConfig;
import org.keycloak.testframework.annotations.KeycloakIntegrationTest;

@KeycloakIntegrationTest(config = IpInfoServerConfig.class)
class IpInfoProviderTest extends BaseGeoIpProviderTest {
}
