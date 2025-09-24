package org.b2code.geoip;

import org.b2code.base.BaseTest;
import org.b2code.config.InfinispanCacheServerConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.testframework.annotations.KeycloakIntegrationTest;

@KeycloakIntegrationTest(config = InfinispanCacheServerConfig.class)
class InfinispanCacheTest extends BaseTest {

    @Test
    void testKeycloakStarted() {
        RealmRepresentation representation = realm.admin().toRepresentation();
        Assertions.assertNotNull(representation);
    }

    @Test
    void testLogin() {
        login();
    }

}
