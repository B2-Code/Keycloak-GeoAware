package org.b2code.geoip;

import org.b2code.config.MaxmindWebserviceServerConfig;
import org.keycloak.testframework.annotations.KeycloakIntegrationTest;

@KeycloakIntegrationTest(config = MaxmindWebserviceServerConfig.class)
class MaxmindWebserviceProviderTest extends BaseGeoIpProviderTest {
}
