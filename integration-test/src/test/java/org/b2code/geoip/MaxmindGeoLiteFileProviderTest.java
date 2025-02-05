package org.b2code.geoip;

import org.b2code.config.MaxmindGeoLiteFileServerConfig;
import org.keycloak.testframework.annotations.KeycloakIntegrationTest;

@KeycloakIntegrationTest(config = MaxmindGeoLiteFileServerConfig.class)
public class MaxmindGeoLiteFileProviderTest extends MaxmindFileProviderTest {

}
