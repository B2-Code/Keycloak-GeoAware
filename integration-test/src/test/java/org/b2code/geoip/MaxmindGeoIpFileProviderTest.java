package org.b2code.geoip;

import org.b2code.config.MaxmindGeoIpFileServerConfig;
import org.keycloak.testframework.annotations.KeycloakIntegrationTest;

@KeycloakIntegrationTest(config = MaxmindGeoIpFileServerConfig.class)
public class MaxmindGeoIpFileProviderTest extends MaxmindFileProviderTest {

}
