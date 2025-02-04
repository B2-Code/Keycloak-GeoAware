package org.b2code.geoip;

import org.b2code.config.IpDataServerConfig;
import org.keycloak.testframework.annotations.KeycloakIntegrationTest;

@KeycloakIntegrationTest(config = IpDataServerConfig.class)
public class IpDataProviderTest extends BaseGeoIpProviderTest {
}
